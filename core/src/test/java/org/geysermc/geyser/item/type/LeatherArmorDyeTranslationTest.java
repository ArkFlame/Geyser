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

package org.geysermc.geyser.item.type;

import org.cloudburstmc.nbt.NbtMap;
import org.geysermc.geyser.scoreboard.network.util.GeyserMockContext;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

/**
 * Regression test for GeyserMC/Geyser#1922: a dyed (tinted) leather chestplate taken from
 * Bedrock's creative inventory must keep its colour when translated back into a Java data
 * component patch. The creative path is
 * PlayerInventoryTranslator.translateCreativeRequest -> ItemTranslator.translateToJava
 * -> Item.translateNbtToJava, where Bedrock's customColor NBT must become DYED_COLOR.
 */
class LeatherArmorDyeTranslationTest {

    // Arbitrary RGB fixture value, not wire data; used only to prove the value is carried through.
    private static final int DYE_COLOR = 0x3F76E4;

    @Test
    void creativeCustomColorBecomesDyedColorComponent() {
        GeyserMockContext.mockContext(() -> {
            ArmorItem leatherChestplate = mock(ArmorItem.class, CALLS_REAL_METHODS);
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            DataComponents components = new DataComponents(new HashMap<>());

            NbtMap bedrockNbt = NbtMap.builder().putInt("customColor", DYE_COLOR).build();
            leatherChestplate.translateNbtToJava(session, bedrockNbt, components, null);

            // FAILS on current code: translateNbtToJava is an empty stub, so this is null.
            assertEquals(Integer.valueOf(DYE_COLOR), components.get(DataComponentTypes.DYED_COLOR));
        });
    }

    @Test
    void absentCustomColorDoesNotInventDyedColor() {
        GeyserMockContext.mockContext(() -> {
            ArmorItem leatherChestplate = mock(ArmorItem.class, CALLS_REAL_METHODS);
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            DataComponents components = new DataComponents(new HashMap<>());

            leatherChestplate.translateNbtToJava(session, NbtMap.EMPTY, components, null);

            assertNull(components.get(DataComponentTypes.DYED_COLOR));
        });
    }

    @Test
    void blackZeroColorIsNotTreatedAsAbsent() {
        GeyserMockContext.mockContext(() -> {
            ArmorItem leatherChestplate = mock(ArmorItem.class, CALLS_REAL_METHODS);
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            DataComponents components = new DataComponents(new HashMap<>());

            // 0x000000 is a valid dye colour; a sentinel-based fix (e.g. getInt(..., -1))
            // would drop it. containsKey() is required for correctness.
            NbtMap bedrockNbt = NbtMap.builder().putInt("customColor", 0).build();
            leatherChestplate.translateNbtToJava(session, bedrockNbt, components, null);

            assertEquals(Integer.valueOf(0), components.get(DataComponentTypes.DYED_COLOR));
        });
    }

    @Test
    void fireworkStarCustomColorIsNotADyedColor() {
        GeyserMockContext.mockContext(() -> {
            FireworkStarItem star = mock(FireworkStarItem.class, CALLS_REAL_METHODS);
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            DataComponents components = new DataComponents(new HashMap<>());

            // Bedrock reuses customColor for a firework star's colour; it is not a dye.
            star.translateNbtToJava(session, NbtMap.builder().putInt("customColor", DYE_COLOR).build(), components, null);

            assertNull(components.get(DataComponentTypes.DYED_COLOR));
        });
    }
}
