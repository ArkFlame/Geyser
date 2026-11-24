/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.session.cache;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.packet.ClientboundCloseFormPacket;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.cloudburstmc.protocol.bedrock.packet.NpcDialoguePacket;
import org.cloudburstmc.protocol.bedrock.packet.NpcRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAttributesPacket;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.form.impl.FormDefinitions;
import org.geysermc.cumulus.form.impl.simple.SimpleFormDefinition;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.api.bedrock.form.NpcDialogueForm;
import org.geysermc.geyser.entity.attribute.GeyserAttributeType;
import org.geysermc.geyser.entity.type.Entity;
import org.geysermc.geyser.entity.type.LivingEntity;
import org.geysermc.geyser.session.GeyserSession;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class FormCache {
    /**
     * The magnitude of this doesn't actually matter, but it must be negative so that
     * BedrockNetworkStackLatencyTranslator can detect the hack.
     */
    private static final long MAGIC_FORM_IMAGE_HACK_TIMESTAMP = -1234567890L;
    /**
     * Shortest intervals between settings form builds and container close resends
     */
    private static final long SETTINGS_REQUEST_COOLDOWN_MILLIS = 1000;
    private static final long CONTAINER_CLOSE_RESEND_COOLDOWN_MILLIS = 500;

    private final FormDefinitions formDefinitions = FormDefinitions.instance();
    private final AtomicInteger formIdCounter = new AtomicInteger(0);
    private final Int2ObjectMap<Form> forms = new Int2ObjectOpenHashMap<>();
    private final GeyserSession session;
    private long lastSettingsRequest;
    private long lastContainerCloseResend;

    public boolean hasFormOpen() {
        // If forms is empty it implies that there are no forms to show
        // so technically this returns "has forms to show" or "has open"
        // Forms are only queued in specific circumstances, such as waiting on
        // previous inventories to close
        return !forms.isEmpty();
    }

    public int addForm(Form form) {
        int formId = formIdCounter.getAndIncrement();
        forms.put(formId, form);
        return formId;
    }

    public void showForm(Form form) {
        int formId = addForm(form);

        if (session.getUpstream().isInitialized()) {
            sendForm(formId, form);
        }
    }

    private void sendForm(int formId, Form form) {
        if (form instanceof NpcDialogueForm npcForm) {
            sendNpcDialogue(formId, npcForm);
            return;
        }

        String jsonData = formDefinitions.codecFor(form).jsonData(form);

        ModalFormRequestPacket formRequestPacket = new ModalFormRequestPacket();
        formRequestPacket.setFormId(formId);
        formRequestPacket.setFormData(jsonData);
        session.sendUpstreamPacket(formRequestPacket);

        // Hack to fix the (url) image loading bug
        if (form instanceof SimpleForm) {
            // Two delays:
            // First, 500ms, before we send the network stack latency packet
            session.scheduleInEventLoop(() -> session.sendNetworkLatencyStackPacket(MAGIC_FORM_IMAGE_HACK_TIMESTAMP, false, () -> {
                    // Then, wait 500ms after we receive the response, then update attributes to get the image to show
                    session.scheduleInEventLoop(() -> {
                        // Hack to fix the url image loading bug
                        UpdateAttributesPacket attributesPacket = new UpdateAttributesPacket();
                        attributesPacket.setRuntimeEntityId(session.getPlayerEntity().geyserId());

                        AttributeData attribute = session.getPlayerEntity().getAttributes().get(GeyserAttributeType.EXPERIENCE_LEVEL);
                        if (attribute != null) {
                            attributesPacket.setAttributes(Collections.singletonList(attribute));
                        } else {
                            attributesPacket.setAttributes(Collections.singletonList(GeyserAttributeType.EXPERIENCE_LEVEL.getAttribute(0)));
                        }

                        session.sendUpstreamPacket(attributesPacket);
                    }, 500, TimeUnit.MILLISECONDS);
                }), 500, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Opens a plugin-provided NPC dialogue. Kept gated so that the default configuration keeps the
     * pre-dialogue form behaviour, and so that clients that do not expect these packets are unaffected.
     */
    private void sendNpcDialogue(int formId, NpcDialogueForm dialogue) {
        if (!session.getGeyser().config().gameplay().enableNpcDialogues()) {
            forms.remove(formId);
            return;
        }

        Entity entity = session.getEntityCache().getEntityByUuid(dialogue.entityUuid());
        if (!(entity instanceof LivingEntity)) {
            forms.remove(formId);
            GeyserImpl.getInstance().getLogger().error("Cannot open an NPC dialogue for unknown or non-living entity " + dialogue.entityUuid());
            return;
        }

        entity.getMetadata().put(EntityDataTypes.HAS_NPC, true);
        entity.getMetadata().put(EntityDataTypes.NPC_DATA, dialogue.npcData());
        entity.updateBedrockMetadata();
        session.setNpcId(entity.geyserId());

        NpcDialoguePacket packet = new NpcDialoguePacket();
        packet.setNpcName(dialogue.title());
        packet.setDialogue(dialogue.content());
        // Not visible to the player, so it doubles as the form ID for the response
        packet.setSceneName(String.valueOf(formId));
        packet.setAction(NpcDialoguePacket.Action.OPEN);
        packet.setUniqueEntityId(entity.geyserId());
        packet.setActionJson(dialogue.actionJson());

        // The dialogue will not open unless the adventure settings update precedes it
        session.scheduleInEventLoop(() -> {
            session.sendAdventureSettings();
            session.sendUpstreamPacket(packet);
        }, 200, TimeUnit.MILLISECONDS);
    }

    /**
     * @return whether a settings form may be built for a request now
     */
    public boolean allowSettingsRequest() {
        long now = System.currentTimeMillis();
        if (now - lastSettingsRequest < SETTINGS_REQUEST_COOLDOWN_MILLIS) {
            return false;
        }
        lastSettingsRequest = now;
        return true;
    }

    /**
     * Resends pending forms after the client closed a container, which would have hidden them.
     */
    public void resendAfterContainerClose() {
        long now = System.currentTimeMillis();
        if (forms.isEmpty() || now - lastContainerCloseResend < CONTAINER_CLOSE_RESEND_COOLDOWN_MILLIS) {
            return;
        }
        lastContainerCloseResend = now;
        resendAllForms();
    }

    public void resendAllForms() {
        // Send from a copy: sending an NPC dialogue may drop an invalid form from the cache
        for (Int2ObjectMap.Entry<Form> entry : new Int2ObjectOpenHashMap<>(forms).int2ObjectEntrySet()) {
            sendForm(entry.getIntKey(), entry.getValue());
        }
    }

    /**
     * @return whether an NPC dialogue is currently cached
     */
    public boolean hasNpcFormOpen() {
        for (Form form : forms.values()) {
            if (form instanceof NpcDialogueForm) {
                return true;
            }
        }
        return false;
    }

    private void completeForm(Form form, String response) {
        try {
            if (form instanceof NpcDialogueForm dialogue) {
                SimpleFormDefinition.instance().handleFormResponse(dialogue.form(), response);
            } else {
                formDefinitions.definitionFor(form).handleFormResponse(form, response);
            }
        } catch (Exception e) {
            GeyserImpl.getInstance().getLogger().error("Error while processing form response!", e);
        }
    }

    public void handleResponse(ModalFormResponsePacket response) {
        Form form = forms.remove(response.getFormId());
        if (form == null) {
            return;
        }

        completeForm(form, response.getFormData());
    }

    /**
     * Handles a response to an NPC dialogue. The scene name carries the form ID and the action type carries
     * the selected button index.
     */
    public void handleResponse(NpcRequestPacket response) {
        int formId;
        try {
            formId = Integer.parseInt(response.getSceneName());
        } catch (NumberFormatException e) {
            return;
        }
        Form form = forms.remove(formId);
        if (form == null) {
            return;
        }

        completeForm(form, String.valueOf(response.getActionType()));
    }

    public void closeForms() {
        if (!this.forms.isEmpty()) {
            // Copy them to ensure any response handler's sent form isn't instantly cleared
            Int2ObjectMap<Form> copy = new Int2ObjectOpenHashMap<>(this.forms);
            this.forms.clear();
            // Now close it
            if (session.getNpcId() != -1L && session.getGeyser().config().gameplay().enableNpcDialogues()) {
                NpcDialoguePacket closeDialogue = new NpcDialoguePacket();
                closeDialogue.setAction(NpcDialoguePacket.Action.CLOSE);
                closeDialogue.setSceneName("");
                closeDialogue.setDialogue("");
                closeDialogue.setActionJson("");
                closeDialogue.setNpcName("");
                closeDialogue.setUniqueEntityId(session.getNpcId());
                session.sendUpstreamPacket(closeDialogue);
                session.setNpcId(-1L);
            }
            session.sendUpstreamPacket(new ClientboundCloseFormPacket());

            for (Form form : copy.values()) {
                completeForm(form, "");
            }
        }
    }
}
