package org.geysermc.geyser.translator.item;

import net.kyori.adventure.key.Key;
import org.geysermc.geyser.api.block.custom.CustomBlockData;
import org.geysermc.geyser.item.type.Item;
import org.geysermc.geyser.registry.BlockRegistries;
import org.geysermc.geyser.registry.type.CustomBlockItemOverride;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** The empty-registry guard must return exactly what CustomBlockItemTranslator.resolve returned before. */
class CustomBlockOverrideGuardParityTest {

    @AfterEach
    void cleanup() {
        BlockRegistries.CUSTOM_BLOCK_ITEM_OVERRIDES.get().clear();
    }

    private static Object guard(Item item, Key model) {
        return CustomBlockItemTranslator.resolveForItem(item, model);
    }

    @Test
    void emptyRegistryYieldsNullLikeBefore() {
        Item item = mock(Item.class);
        when(item.javaKey()).thenReturn(Key.key("minecraft", "stone"));
        BlockRegistries.CUSTOM_BLOCK_ITEM_OVERRIDES.get().clear();
        assertNull(CustomBlockItemTranslator.resolve(BlockRegistries.CUSTOM_BLOCK_ITEM_OVERRIDES.get(), "minecraft:stone", null));
        assertNull(guard(item, null));
        assertNull(guard(item, Key.key("pack", "model")));
    }

    @Test
    void populatedRegistryMatchesDirectResolve() {
        Item item = mock(Item.class);
        when(item.javaKey()).thenReturn(Key.key("minecraft", "stone"));
        CustomBlockData base = mock(CustomBlockData.class);
        CustomBlockData specific = mock(CustomBlockData.class);
        Key model = Key.key("pack", "model");
        BlockRegistries.CUSTOM_BLOCK_ITEM_OVERRIDES.get().put(CustomBlockItemOverride.base("minecraft:stone"), base);
        BlockRegistries.CUSTOM_BLOCK_ITEM_OVERRIDES.get().put(new CustomBlockItemOverride("minecraft:stone", model), specific);

        for (Key k : new Key[]{null, model, Key.key("pack", "other")}) {
            Object direct = CustomBlockItemTranslator.resolve(BlockRegistries.CUSTOM_BLOCK_ITEM_OVERRIDES.get(), "minecraft:stone", k);
            assertSame(direct, guard(item, k));
        }
        assertSame(specific, guard(item, model));
        assertSame(base, guard(item, null));
    }
}
