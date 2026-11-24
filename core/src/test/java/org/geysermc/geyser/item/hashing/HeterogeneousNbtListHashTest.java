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

package org.geysermc.geyser.item.hashing;

import com.google.common.hash.HashCode;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.geysermc.adventure.text.serializer.nbt.HeterogeneousNbtList;
import org.geysermc.geyser.registry.java.JavaRegistryProvider;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

class HeterogeneousNbtListHashTest {
    private final MinecraftHashEncoder encoder = new MinecraftHashEncoder(mock(JavaRegistryProvider.class));

    @Test
    void heterogeneousListHashesEveryElement() {
        NbtMap compound = NbtMap.builder().putString("with", "a").build();
        NbtList<?> list = HeterogeneousNbtList.of(compound, "string");

        assertEquals(encoder.list(List.of(encoder.nbtMap(compound), encoder.string("string"))),
            encoder.nbtList(list));
        assertNotEquals(encoder.list(List.of(encoder.nbtMap(compound))), encoder.nbtList(list));
    }

    @Test
    void heterogeneousListIsHashedFromContainingMap() {
        NbtMap compound = NbtMap.builder().putString("with", "a").build();
        NbtList<?> list = HeterogeneousNbtList.of(compound, "string");
        NbtMapBuilder builder = NbtMap.builder();
        builder.put("a heterogeneous list", list);
        NbtMap map = builder.build();

        HashCode expected = encoder.map(Map.of(
            encoder.string("a heterogeneous list"), encoder.nbtList(list)));
        assertEquals(expected, encoder.nbtMap(map));
    }
}
