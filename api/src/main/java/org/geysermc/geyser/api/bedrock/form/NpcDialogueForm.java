/*
 * Copyright (c) 2026 GeyserMC. http://geysermc.org
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

package org.geysermc.geyser.api.bedrock.form;

import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A plugin-facing NPC dialogue.
 *
 * <p>Cumulus 1.1 has no {@code NpcForm}, so this type is an additive bridge: it is a regular
 * {@link Form} that plugins pass to {@code Connection#sendForm(Form)}, while Geyser renders it with
 * Bedrock's {@code NpcDialoguePacket}. Button responses use Cumulus's {@link SimpleForm} response
 * model, so existing response handlers keep working.</p>
 *
 * <p>Sending is only honoured while the {@code npc-dialogues} gameplay option is enabled.</p>
 */
public final class NpcDialogueForm implements Form {
    private final SimpleForm form;
    private final UUID entityUuid;
    private final String actionJson;
    private final String npcData;

    private NpcDialogueForm(Builder builder) {
        this.entityUuid = Objects.requireNonNull(builder.entityUuid, "entityUuid");

        SimpleForm.Builder formBuilder = SimpleForm.builder()
                .title(builder.title)
                .content(builder.content);
        for (String button : builder.buttons) {
            formBuilder.button(button);
        }
        Consumer<SimpleFormResponse> handler = builder.responseHandler;
        formBuilder.validResultHandler(handler != null ? handler : response -> { });
        this.form = formBuilder.build();

        this.actionJson = builder.actionJson;
        this.npcData = builder.npcData;
    }

    /**
     * Creates a new builder for an NPC dialogue.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the Cumulus form that carries this dialogue's title, content, buttons and response handler.
     * Geyser uses it to adapt the NPC response onto the existing Cumulus form response pipeline.
     *
     * @return the backing Cumulus form
     */
    public SimpleForm form() {
        return form;
    }

    @Override
    public String title() {
        return form.title();
    }

    /**
     * @return the dialogue text shown to the player
     */
    public String content() {
        return form.content();
    }

    /**
     * @return the labels of the dialogue's buttons, in response order
     */
    public List<String> buttons() {
        List<String> labels = new ArrayList<>(form.buttons().size());
        form.buttons().forEach(button -> labels.add(button.text()));
        return labels;
    }

    /**
     * @return the UUID of the entity that owns this dialogue
     */
    public UUID entityUuid() {
        return entityUuid;
    }

    /**
     * @return the raw action JSON sent to the client
     */
    public String actionJson() {
        return actionJson;
    }

    /**
     * @return the raw NPC data sent in the entity metadata
     */
    public String npcData() {
        return npcData;
    }

    /**
     * Builder for {@link NpcDialogueForm}.
     */
    public static final class Builder {
        private String title = "";
        private String content = "";
        private final List<String> buttons = new ArrayList<>();
        private UUID entityUuid;
        private String actionJson = "";
        private String npcData = "";
        private Consumer<SimpleFormResponse> responseHandler;

        private Builder() {
        }

        /**
         * @param title the NPC name shown above the dialogue
         * @return this builder
         */
        public Builder title(String title) {
            this.title = Objects.requireNonNull(title, "title");
            return this;
        }

        /**
         * @param content the dialogue text
         * @return this builder
         */
        public Builder content(String content) {
            this.content = Objects.requireNonNull(content, "content");
            return this;
        }

        /**
         * Adds an action button. The button index is passed to the response handler.
         *
         * @param label the button label
         * @return this builder
         */
        public Builder button(String label) {
            this.buttons.add(Objects.requireNonNull(label, "label"));
            return this;
        }

        /**
         * @param entityUuid the UUID of a living entity the dialogue belongs to
         * @return this builder
         */
        public Builder entityUuid(UUID entityUuid) {
            this.entityUuid = Objects.requireNonNull(entityUuid, "entityUuid");
            return this;
        }

        /**
         * @param actionJson the raw action JSON sent to the client
         * @return this builder
         */
        public Builder actionJson(String actionJson) {
            this.actionJson = Objects.requireNonNull(actionJson, "actionJson");
            return this;
        }

        /**
         * @param npcData the raw NPC data sent in the entity metadata
         * @return this builder
         */
        public Builder npcData(String npcData) {
            this.npcData = Objects.requireNonNull(npcData, "npcData");
            return this;
        }

        /**
         * @param responseHandler invoked with the selected button, as with a Cumulus simple form
         * @return this builder
         */
        public Builder validResultHandler(Consumer<SimpleFormResponse> responseHandler) {
            this.responseHandler = Objects.requireNonNull(responseHandler, "responseHandler");
            return this;
        }

        /**
         * @return the built dialogue
         */
        public NpcDialogueForm build() {
            return new NpcDialogueForm(this);
        }
    }
}
