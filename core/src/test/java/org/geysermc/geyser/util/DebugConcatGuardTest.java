package org.geysermc.geyser.util;

import org.cloudburstmc.nbt.NbtMap;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.GeyserLogger;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** With debug off, debug messages must not be built at all (the old code concatenated them eagerly); with debug on, the same message is logged. */
class DebugConcatGuardTest {

    private static final class CountingToString {
        final AtomicInteger calls = new AtomicInteger();

        @Override
        public String toString() {
            calls.incrementAndGet();
            return "payload";
        }
    }

    private static NbtMap dataWith(Object value) {
        // NbtMap only holds NBT types; a Map-typed raw lookup is not needed, so build via a raw map for the unexpected-type path
        return mock(NbtMap.class, invocation -> invocation.getMethod().getName().equals("get") ? value : null);
    }

    @Test
    void debugOffBuildsNoMessage() {
        GeyserLogger logger = mock(GeyserLogger.class);
        when(logger.isDebug()).thenReturn(false);
        GeyserImpl geyser = mock(GeyserImpl.class);
        when(geyser.getLogger()).thenReturn(logger);
        CountingToString payload = new CountingToString();

        try (MockedStatic<GeyserImpl> impl = mockStatic(GeyserImpl.class)) {
            impl.when(GeyserImpl::getInstance).thenReturn(geyser);
            assertEquals("", SoundUtils.readSoundEvent(dataWith(payload), "ctx"));
        }
        assertEquals(0, payload.calls.get());
        verify(logger, never()).debug(anyString());
    }

    @Test
    void debugOnLogsSameMessage() {
        GeyserLogger logger = mock(GeyserLogger.class);
        when(logger.isDebug()).thenReturn(true);
        GeyserImpl geyser = mock(GeyserImpl.class);
        when(geyser.getLogger()).thenReturn(logger);
        CountingToString payload = new CountingToString();

        try (MockedStatic<GeyserImpl> impl = mockStatic(GeyserImpl.class)) {
            impl.when(GeyserImpl::getInstance).thenReturn(geyser);
            assertEquals("", SoundUtils.readSoundEvent(dataWith(payload), "ctx"));
        }
        assertEquals(1, payload.calls.get());
        verify(logger).debug("Sound event for ctx was of an unexpected type! Expected string or NBT map, got payload");
    }
}
