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
package org.geysermc.geyser.translator.protocol.bedrock;

import org.cloudburstmc.protocol.bedrock.data.NpcRequestType;
import org.cloudburstmc.protocol.bedrock.packet.NpcDialoguePacket;
import org.cloudburstmc.protocol.bedrock.packet.NpcRequestPacket;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.protocol.PacketTranslator;
import org.geysermc.geyser.translator.protocol.Translator;

/**
 * Handles the responses a Bedrock client sends when it interacts with an NPC dialogue.
 */
@Translator(packet = NpcRequestPacket.class)
public class BedrockNpcRequestTranslator extends PacketTranslator<NpcRequestPacket> {

    @Override
    public void translate(GeyserSession session, NpcRequestPacket packet) {
        if (!session.getGeyser().config().gameplay().enableNpcDialogues()) {
            return;
        }

        session.setNpcId(packet.getRuntimeEntityId());

        if (packet.getRequestType() == NpcRequestType.EXECUTE_COMMAND_ACTION) {
            // Acknowledge the button press and close the dialogue on the client.
            NpcDialoguePacket closeDialogue = new NpcDialoguePacket();
            closeDialogue.setAction(NpcDialoguePacket.Action.CLOSE);
            closeDialogue.setSceneName(packet.getSceneName());
            closeDialogue.setDialogue("");
            closeDialogue.setActionJson("");
            closeDialogue.setNpcName("");
            closeDialogue.setUniqueEntityId(packet.getRuntimeEntityId());
            session.sendUpstreamPacket(closeDialogue);

            session.getFormCache().handleResponse(packet);
            session.sendAdventureSettings();
        } else if (packet.getRequestType() == NpcRequestType.EXECUTE_CLOSING_COMMANDS) {
            session.closeForm();
        }
    }
}
