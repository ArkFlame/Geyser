/*
 * Copyright (c) 2025-2026 GeyserMC. http://geysermc.org
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

package org.geysermc.geyser.item.tooltip;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.geysermc.geyser.inventory.item.Potion;
import org.geysermc.geyser.item.Items;
import org.geysermc.geyser.item.TooltipOptions;
import org.geysermc.geyser.item.tooltip.providers.AdventureModeTooltip;
import org.geysermc.geyser.item.tooltip.providers.PotionContentsTooltip;
import org.geysermc.geyser.item.tooltip.providers.SpawnerTooltip;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.AdventureModePredicate;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.HolderSet;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.TypedEntityData;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.BlockEntityType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TooltipProvidersTest {

    private static TooltipContext context(org.geysermc.geyser.item.type.Item item) {
        DataComponents components = new DataComponents(new HashMap<>());
        return new TooltipContext(Optional.empty(), false, true, item, components,
            TooltipOptions.fromComponents(components));
    }

    @Test
    void builtInPotionEffectsAreListed() {
        List<Component> lines = new ArrayList<>();
        new PotionContentsTooltip().addTooltip(context(Items.POTION), lines::add, Potion.NIGHT_VISION.toComponent());

        assertEquals(List.of(Component.translatable("potion.withDuration",
            Component.translatable("effect.minecraft.night_vision"), Component.text("03:00"))), lines);
    }

    @Test
    void potionWithoutEffectsReportsNone() {
        List<Component> lines = new ArrayList<>();
        new PotionContentsTooltip().addTooltip(context(Items.POTION), lines::add, Potion.WATER.toComponent());

        assertEquals(List.of(Component.translatable("effect.none").color(NamedTextColor.GRAY)), lines);
    }

    @Test
    void canPlaceOnUsesTheTagWhenNoSessionIsAvailable() {
        AdventureModePredicate predicate = AdventureModePredicate.builder().predicates(List.of(
            AdventureModePredicate.BlockPredicate.builder().blocks(new HolderSet(Key.key("minecraft:logs"))).build()
        )).build();
        List<Component> lines = new ArrayList<>();
        new AdventureModeTooltip("item.canPlace").addTooltip(context(Items.STONE), lines::add, predicate);

        assertEquals(List.of(Component.translatable("item.canPlace")
            .append(Component.text(" #minecraft:logs")).color(NamedTextColor.GRAY)), lines);
    }

    @Test
    void spawnerTooltipOnlyAppliesToSpawnerItems() {
        TypedEntityData<BlockEntityType> data = TypedEntityData.<BlockEntityType>builder().tag(org.cloudburstmc.nbt.NbtMap.EMPTY).build();
        List<Component> lines = new ArrayList<>();
        new SpawnerTooltip().addTooltip(context(Items.SPAWNER), lines::add, data);

        assertEquals(List.of(
            Component.translatable("block.minecraft.spawner.desc1").color(NamedTextColor.GRAY),
            Component.translatable("block.minecraft.spawner.desc2").color(NamedTextColor.GRAY)
        ), lines);

        lines.clear();
        new SpawnerTooltip().addTooltip(context(Items.STONE), lines::add, data);
        assertTrue(lines.isEmpty());
    }

    @Test
    void nameProvidersAreDispatchedOnlyWhenEnabled() {
        TooltipProviders.registerName(DataComponentTypes.CUSTOM_NAME,
            (ctx, adder, name) -> adder.accept(name));
        DataComponents components = new DataComponents(new HashMap<>());
        components.put(DataComponentTypes.CUSTOM_NAME, Component.text("Named"));
        TooltipContext context = context(Items.STONE);
        List<String> lines = new ArrayList<>();

        boolean previous = TooltipProviders.isEnabled();
        try {
            TooltipProviders.setEnabled(false);
            TooltipProviders.addNameTooltips(context, components, lines::add);
            assertTrue(lines.isEmpty());

            TooltipProviders.setEnabled(true);
            TooltipProviders.addNameTooltips(context, components, lines::add);
            assertEquals(List.of("Named"), lines);
        } finally {
            TooltipProviders.setEnabled(previous);
        }
    }
}
