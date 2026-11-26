package org.geysermc.geyser.translator.protocol.java;

import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.GeyserLogger;
import org.geysermc.geyser.entity.type.Entity;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.protocol.java.entity.JavaEntityEventTranslator;
import org.geysermc.mcprotocollib.protocol.data.game.entity.EntityEventType;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundEntityEventPacket;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** An unhandled-event debug message embeds the whole packet; with debug off it must not be stringified. */
class UnhandledPacketDebugGuardTest {

    private static void run(boolean debug, AtomicInteger toStringCalls, GeyserLogger logger) {
        when(logger.isDebug()).thenReturn(debug);
        GeyserImpl geyser = mock(GeyserImpl.class);
        when(geyser.getLogger()).thenReturn(logger);
        GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
        Entity entity = mock(Entity.class, RETURNS_DEEP_STUBS);
        when(session.getEntityCache().getEntityByJavaId(4)).thenReturn(entity);

        ClientboundEntityEventPacket packet = mock(ClientboundEntityEventPacket.class);
        when(packet.getEntityId()).thenReturn(4);
        when(packet.getEvent()).thenReturn(EntityEventType.ARMOR_STAND_HIT);
        doAnswer(i -> {
            toStringCalls.incrementAndGet();
            return "PACKET";
        }).when(packet).toString();

        try (MockedStatic<GeyserImpl> impl = mockStatic(GeyserImpl.class)) {
            impl.when(GeyserImpl::getInstance).thenReturn(geyser);
            new JavaEntityEventTranslator().translate(session, packet);
        }
    }

    @Test
    void debugOffDoesNotStringifyPacket() {
        AtomicInteger calls = new AtomicInteger();
        GeyserLogger logger = mock(GeyserLogger.class);
        run(false, calls, logger);
        assertEquals(0, calls.get());
        verify(logger, never()).debug(anyString());
    }

    @Test
    void debugOnLogsSameMessage() {
        AtomicInteger calls = new AtomicInteger();
        GeyserLogger logger = mock(GeyserLogger.class);
        run(true, calls, logger);
        assertEquals(1, calls.get());
        verify(logger).debug("unhandled entity event: PACKET");
    }
}
