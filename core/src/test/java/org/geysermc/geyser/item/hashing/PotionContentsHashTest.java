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

import org.cloudburstmc.nbt.NbtMap;
import org.geysermc.geyser.registry.java.JavaRegistryProvider;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.PotionContents;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

class PotionContentsHashTest {
    private final MinecraftHashEncoder encoder = new MinecraftHashEncoder(mock(JavaRegistryProvider.class));

    @Test
    void presentSignedColorsAreIncludedInTheHash() {
        var hasher = DataComponentHashers.hasher(DataComponentTypes.POTION_CONTENTS);
        for (int color : new int[]{-1, Integer.MIN_VALUE, 0xff336699, 0}) {
            var contents = new PotionContents(-1, OptionalInt.of(color), List.of(), null);
            assertEquals(encoder.nbtMap(NbtMap.builder().putInt("custom_color", color).build()),
                hasher.hash(contents, encoder));
        }
    }

    @Test
    void absentColorDiffersFromPresentMinusOne() {
        var hasher = DataComponentHashers.hasher(DataComponentTypes.POTION_CONTENTS);
        var absent = new PotionContents(-1, -1, List.of(), null);
        var present = new PotionContents(-1, OptionalInt.of(-1), List.of(), null);
        assertEquals(encoder.nbtMap(NbtMap.EMPTY), hasher.hash(absent, encoder));
        assertNotEquals(hasher.hash(absent, encoder), hasher.hash(present, encoder));
    }
}
