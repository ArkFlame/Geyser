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
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Remembers what the Java server told us about each map, so that tooltips can show the same
 * scale, level and locked lines Java does. Both values come straight from
 * {@code ClientboundMapItemDataPacket}; nothing here is derived or invented.
 */
public final class MapCache {
    /**
     * The map state Java's tooltip reads: its zoom level (0 to 4) and whether it is locked.
     */
    public record MapInfo(byte scale, boolean locked) {
    }

    private final Int2ObjectMap<MapInfo> maps = new Int2ObjectOpenHashMap<>();

    public synchronized void update(int mapId, byte scale, boolean locked) {
        this.maps.put(mapId, new MapInfo(scale, locked));
    }

    /**
     * @return the last state the server sent for this map, or null if it never sent one
     */
    public synchronized @Nullable MapInfo get(int mapId) {
        return this.maps.get(mapId);
    }
}
