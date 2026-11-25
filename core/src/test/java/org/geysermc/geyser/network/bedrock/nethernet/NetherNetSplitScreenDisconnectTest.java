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

package org.geysermc.geyser.network.bedrock.nethernet;

import io.netty.channel.Channel;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.geysermc.geyser.event.type.SessionDisconnectEventImpl;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.session.UpstreamSession;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Split-screen: one console owns one NetherNet channel, so a guest leaving must not release it
 * and eject the host and the other local players.
 */
class NetherNetSplitScreenDisconnectTest {
    private static SessionDisconnectEventImpl disconnectEvent(Channel sharedChannel, boolean subClient) {
        BedrockPeer peer = mock(BedrockPeer.class);
        when(peer.getChannel()).thenReturn(sharedChannel);

        BedrockServerSession bedrock = mock(BedrockServerSession.class);
        when(bedrock.getPeer()).thenReturn(peer);
        when(bedrock.isSubClient()).thenReturn(subClient);

        UpstreamSession upstream = mock(UpstreamSession.class);
        when(upstream.getSession()).thenReturn(bedrock);

        GeyserSession session = mock(GeyserSession.class);
        when(session.getUpstream()).thenReturn(upstream);

        SessionDisconnectEventImpl event = mock(SessionDisconnectEventImpl.class);
        when(event.connection()).thenReturn(session);
        return event;
    }

    private static void dispatch(SessionDisconnectEventImpl event) throws Exception {
        // Skip the constructor (it starts transports); the handler only reads the event and a null provider client.
        NetherNetServer server = mock(NetherNetServer.class, CALLS_REAL_METHODS);
        Method handler = NetherNetServer.class.getDeclaredMethod("onSessionDisconnect", SessionDisconnectEventImpl.class);
        handler.setAccessible(true);
        handler.invoke(server, event);
    }

    @Test
    void guestDisconnectDoesNotReleaseTheSharedConsoleChannel() throws Exception {
        Channel shared = mock(Channel.class);
        try (MockedStatic<NetherNetChannelInitialiser> initialiser = mockStatic(NetherNetChannelInitialiser.class)) {
            dispatch(disconnectEvent(shared, true));
            initialiser.verify(() -> NetherNetChannelInitialiser.releaseChannel(any(Channel.class)), never());
        }
    }

    @Test
    void primaryDisconnectStillReleasesItsChannel() throws Exception {
        Channel shared = mock(Channel.class);
        try (MockedStatic<NetherNetChannelInitialiser> initialiser = mockStatic(NetherNetChannelInitialiser.class)) {
            dispatch(disconnectEvent(shared, false));
            initialiser.verify(() -> NetherNetChannelInitialiser.releaseChannel(shared), times(1));
        }
    }
}
