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

package org.geysermc.geyser.item.parser;

import org.cloudburstmc.nbt.NbtMap;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.PotionContents;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Regression for #6914 residual: a present negative (opaque ARGB) potion custom_color in NBT
 * must not be collapsed to "absent" by the NBT parser.
 */
class PotionContentsParserTest {
    private final GeyserSession session = mock(GeyserSession.class);

    private PotionContents parse(NbtMap potionContents) {
        NbtMap components = NbtMap.builder().putCompound("minecraft:potion_contents", potionContents).build();
        DataComponents patch = ItemStackParser.parseDataComponentPatch(session, components);
        assertNotNull(patch);
        PotionContents contents = patch.get(DataComponentTypes.POTION_CONTENTS);
        assertNotNull(contents);
        return contents;
    }

    @Test
    void presentNegativeArgbColorIsKept() {
        PotionContents contents = parse(NbtMap.builder().putInt("custom_color", 0xFF336699).build());
        assertTrue(contents.hasCustomColor(), "a present negative ARGB custom_color must not be collapsed to absent");
        assertEquals(0xFF336699, contents.getCustomColor());
    }

    @Test
    void presentMinusOneIsKept() {
        PotionContents contents = parse(NbtMap.builder().putInt("custom_color", -1).build());
        assertTrue(contents.hasCustomColor());
        assertEquals(-1, contents.getCustomColor());
    }

    @Test
    void presentZeroAndPositiveAreKept() {
        for (int color : new int[]{0, 0x336699}) {
            PotionContents contents = parse(NbtMap.builder().putInt("custom_color", color).build());
            assertTrue(contents.hasCustomColor());
            assertEquals(color, contents.getCustomColor());
        }
    }

    @Test
    void absentColorStaysAbsent() {
        PotionContents contents = parse(NbtMap.builder().putString("custom_name", "x").build());
        assertFalse(contents.hasCustomColor());
    }
}
