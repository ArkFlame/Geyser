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
package org.geysermc.geyser.network;

import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.network.bedrock.InvalidPacketHandler;
import org.geysermc.geyser.network.bedrock.LoginPacketHandler;
import org.geysermc.geyser.network.bedrock.UpstreamPacketHandler;
import org.geysermc.geyser.session.GeyserSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GeyserServerInitializerTest {
    private MockedStatic<GeyserImpl> geyserInstance;
    private MockedConstruction<GeyserSession> sessions;

    @BeforeEach
    void mockSession() {
        GeyserImpl geyser = mock(GeyserImpl.class, RETURNS_DEEP_STUBS);
        geyserInstance = mockStatic(GeyserImpl.class);
        geyserInstance.when(GeyserImpl::getInstance).thenReturn(geyser);
        sessions = mockConstruction(GeyserSession.class, (session, context) ->
                when(session.getNetwork()).thenReturn(mock(GeyserNetwork.class)));
    }

    @AfterEach
    void closeMocks() {
        sessions.close();
        geyserInstance.close();
    }

    @Test
    void subClientLoginStartsAtTheUpstreamHandler() {
        BedrockServerSession upstream = mock(BedrockServerSession.class, RETURNS_DEEP_STUBS);
        when(upstream.isSubClient()).thenReturn(true);

        TestInitializer initializer = new TestInitializer();
        try {
            initializer.initSession(upstream);
        } finally {
            initializer.getEventLoopGroup().shutdownGracefully();
        }

        verify(upstream).setPacketHandler(any(UpstreamPacketHandler.class));
        verify(upstream, never()).setPacketHandler(any(LoginPacketHandler.class));
    }

    @Test
    void primaryStillStartsAtTheLoginHandler() {
        BedrockServerSession upstream = mock(BedrockServerSession.class, RETURNS_DEEP_STUBS);
        when(upstream.getPeer().getChannel().pipeline().get(InvalidPacketHandler.class))
                .thenReturn(mock(InvalidPacketHandler.class));

        TestInitializer initializer = new TestInitializer();
        try {
            initializer.initSession(upstream);
        } finally {
            initializer.getEventLoopGroup().shutdownGracefully();
        }

        verify(upstream).setPacketHandler(any(LoginPacketHandler.class));
        verify(upstream, never()).setPacketHandler(any(UpstreamPacketHandler.class));
    }

    private static final class TestInitializer extends GeyserServerInitializer {
        private TestInitializer() {
            super(mock(GeyserImpl.class, RETURNS_DEEP_STUBS), "test-player-thread");
        }
    }
}
