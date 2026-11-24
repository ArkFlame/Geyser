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

package org.geysermc.geyser.translator.protocol.java.entity;

import org.cloudburstmc.protocol.bedrock.data.entity.EntityEventType;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.packet.EntityEventPacket;
import org.geysermc.geyser.entity.type.Entity;
import org.geysermc.geyser.entity.type.Leashable;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.protocol.PacketTranslator;
import org.geysermc.geyser.translator.protocol.Translator;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundSetEntityLinkPacket;

/**
 * Called when a leash is attached, removed or updated from an entity
 */
@Translator(packet = ClientboundSetEntityLinkPacket.class)
public class JavaSetEntityLinkTranslator extends PacketTranslator<ClientboundSetEntityLinkPacket> {

    @Override
    public void translate(GeyserSession session, ClientboundSetEntityLinkPacket packet) {
        Entity leashedEntity = session.getEntityCache().getEntityByJavaId(packet.getEntityId());
        int holderJavaId = packet.getAttachedToId();

        if (holderJavaId == 0) {
            // Is not being leashed
            if (!(leashedEntity instanceof Leashable asLeashable)) {
                return;
            }
            leashedEntity.setFlag(EntityFlag.LEASHED, false);
            asLeashable.setLeashHolderBedrockId(-1L);
            leashedEntity.updateBedrockMetadata();
            EntityEventPacket eventPacket = new EntityEventPacket();
            eventPacket.setRuntimeEntityId(leashedEntity.geyserId());
            eventPacket.setType(EntityEventType.REMOVE_LEASH);
            eventPacket.setData(0);
            session.sendUpstreamPacket(eventPacket);
            return;
        }

        Entity holderEntity = session.getEntityCache().getEntityByJavaId(holderJavaId);
        if (leashedEntity == null || holderEntity == null) {
            // Java can send this link before either side is tracked (a fence knot is commonly spawned after the
            // mob it holds). Remember it and reapply once the missing entity spawns, otherwise the lead never shows
            // up until the chunk is reloaded and the server sends the link again.
            session.getEntityCache().cachePendingLeash(packet.getEntityId(), holderJavaId);
            return;
        }

        applyLeash(leashedEntity, holderEntity);
    }

    /**
     * Applies a leash link that both entities are now known for. Also used by
     * {@link org.geysermc.geyser.session.cache.EntityCache} when a deferred link can finally be resolved.
     */
    public static void applyLeash(Entity leashedEntity, Entity holderEntity) {
        if (!(leashedEntity instanceof Leashable asLeashable)) {
            return;
        }
        leashedEntity.setFlag(EntityFlag.LEASHED, true);
        asLeashable.setLeashHolderBedrockId(holderEntity.geyserId());
        leashedEntity.updateBedrockMetadata();
    }
}
