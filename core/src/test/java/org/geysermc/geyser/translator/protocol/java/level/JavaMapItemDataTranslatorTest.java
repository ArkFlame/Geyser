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

package org.geysermc.geyser.translator.protocol.java.level;

import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.session.cache.MapCache;
import org.geysermc.mcprotocollib.protocol.data.game.level.map.MapIcon;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundMapItemDataPacket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * #5692 / #6202: a map's zoom level and locked flag arrive in the map packet and must be remembered,
 * because Java's map tooltip is built from them.
 */
class JavaMapItemDataTranslatorTest {
    private final MapCache cache = new MapCache();
    private final GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);

    private void send(int id, byte scale, boolean locked) {
        when(session.getMapCache()).thenReturn(cache);
        new JavaMapItemDataTranslator().translate(session,
            new ClientboundMapItemDataPacket(id, scale, locked, new MapIcon[0]));
    }

    @Test
    void scaleAndLockedAreRemembered() {
        send(42, (byte) 3, true);
        assertEquals(new MapCache.MapInfo((byte) 3, true), cache.get(42));
    }

    @Test
    void laterPacketsReplaceEarlierState() {
        send(7, (byte) 0, false);
        send(7, (byte) 2, true);
        assertEquals(new MapCache.MapInfo((byte) 2, true), cache.get(7));
    }

    @Test
    void mapsAreTrackedIndependentlyAndUnknownMapsAreAbsent() {
        send(1, (byte) 1, false);
        send(2, (byte) 4, true);
        assertEquals(new MapCache.MapInfo((byte) 1, false), cache.get(1));
        assertEquals(new MapCache.MapInfo((byte) 4, true), cache.get(2));
        assertNull(cache.get(3));
    }
}
