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
package org.geysermc.geyser.network.bedrock;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import org.cloudburstmc.protocol.bedrock.BedrockSession;
import org.cloudburstmc.protocol.bedrock.BedrockSessionFactory;
import org.cloudburstmc.protocol.bedrock.PacketDirection;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.packet.SubClientLoginPacket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeyserBedrockPeerTest {
    private final BedrockSessionFactory sessionFactory = mock(BedrockSessionFactory.class);
    private final Channel channel = mock(Channel.class);

    @SuppressWarnings("unchecked")
    private GeyserBedrockPeer peer() {
        Attribute<PacketDirection> direction = mock(Attribute.class);
        when(channel.attr(PacketDirection.ATTRIBUTE)).thenReturn(direction);
        when(direction.get()).thenReturn(PacketDirection.CLIENT_BOUND);
        GeyserBedrockPeer peer = new GeyserBedrockPeer(channel, sessionFactory);
        when(sessionFactory.createSession(eq(peer), anyInt())).thenReturn(mock(BedrockSession.class));
        return peer;
    }

    private static BedrockPacketWrapper wrapper(BedrockPacket packet, int sender, int target) {
        BedrockPacketWrapper wrapper = BedrockPacketWrapper.create();
        wrapper.setPacket(packet);
        wrapper.setSenderSubClientId((byte) sender);
        wrapper.setTargetSubClientId((byte) target);
        return wrapper;
    }

    @Test
    void subClientLoginIsFiledUnderItsSenderSlot() {
        GeyserBedrockPeer peer = peer();
        BedrockPacketWrapper wrapper = wrapper(new SubClientLoginPacket(), 2, 0);

        assertTrue(peer.opensSession(wrapper));
        peer.onBedrockPacket(wrapper);

        assertEquals(2, wrapper.getTargetSubClientId());
    }

    @Test
    void ordinaryGuestPacketsAreAlsoFiledUnderTheirSenderSlot() {
        GeyserBedrockPeer peer = peer();
        BedrockPacketWrapper wrapper = wrapper(new LoginPacket(), 1, 0);

        peer.onBedrockPacket(wrapper);

        assertEquals(1, wrapper.getTargetSubClientId());
    }

    @Test
    void primaryPacketsKeepTheirTarget() {
        GeyserBedrockPeer peer = peer();
        BedrockPacketWrapper wrapper = wrapper(new LoginPacket(), 0, 0);

        peer.onBedrockPacket(wrapper);

        assertEquals(0, wrapper.getTargetSubClientId());
    }
}
