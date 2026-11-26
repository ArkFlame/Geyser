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

package org.geysermc.geyser.item.tooltip;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.geysermc.geyser.item.Items;
import org.geysermc.geyser.item.TooltipOptions;
import org.geysermc.geyser.item.tooltip.providers.MapTooltip;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.session.cache.MapCache;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;
import org.geysermc.geyser.registry.populator.DataComponentRegistryPopulator;
import org.geysermc.geyser.registry.java.BuiltInJavaRegistries;
import org.geysermc.geyser.GeyserResources;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * #5692 / #6202. Expected lines follow vanilla {@code MapId#addToTooltip}: an unknown map says so; the id
 * shows unless the map has a custom name or post-processing; locked shows for a locked map or LOCK
 * post-processing; scale and level show only with advanced tooltips, and SCALE post-processing adds one
 * level, capped at 4.
 */
class MapTooltipTest {
    private static final int MAP = 11;
    private static final int LOCK = 0;
    private static final int SCALE = 1;

    @BeforeAll
    static void bootstrapRegistries() {
        mockContext(() -> {
            DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
        });
    }

    private final MapCache cache = new MapCache();

    private List<Component> lines(boolean advanced, DataComponents components, boolean withSession) {
        GeyserSession session = mock(GeyserSession.class);
        when(session.getMapCache()).thenReturn(cache);
        TooltipContext context = new TooltipContext(withSession ? Optional.of(session) : Optional.empty(), advanced,
            false, Items.FILLED_MAP, components, TooltipOptions.fromComponents(components));
        List<Component> lines = new ArrayList<>();
        new MapTooltip().addTooltip(context, lines::add, MAP);
        return lines;
    }

    private static DataComponents components() {
        return new DataComponents(new HashMap<>());
    }

    private static Component grayKey(String key) {
        return Component.translatable(key).color(NamedTextColor.GRAY);
    }

    private static Component grayArgs(String key, int a, int b) {
        return Component.translatable(key, Component.text(a), Component.text(b)).color(NamedTextColor.GRAY);
    }

    private static Component grayInt(String key, int a) {
        return Component.translatable(key, Component.text(a)).color(NamedTextColor.GRAY);
    }

    @Test
    void lockedMapShowsIdAndLockedWithoutAdvancedTooltips() {
        cache.update(MAP, (byte) 2, true);
        assertEquals(List.of(grayInt("filled_map.id", MAP), grayKey("filled_map.locked")),
            lines(false, components(), true));
    }

    @Test
    void advancedTooltipsAddScaleAndLevel() {
        cache.update(MAP, (byte) 2, false);
        assertEquals(List.of(grayInt("filled_map.id", MAP), grayInt("filled_map.scale", 4),
            grayArgs("filled_map.level", 2, 4)), lines(true, components(), true));
    }

    @Test
    void scalePostProcessingAddsOneLevelAndHidesTheId() {
        cache.update(MAP, (byte) 1, false);
        DataComponents components = components();
        components.put(DataComponentTypes.MAP_POST_PROCESSING, SCALE);
        assertEquals(List.of(grayInt("filled_map.scale", 4), grayArgs("filled_map.level", 2, 4)),
            lines(true, components, true));
    }

    @Test
    void scalePostProcessingIsCappedAtLevelFour() {
        cache.update(MAP, (byte) 4, false);
        DataComponents components = components();
        components.put(DataComponentTypes.MAP_POST_PROCESSING, SCALE);
        assertEquals(List.of(grayInt("filled_map.scale", 16), grayArgs("filled_map.level", 4, 4)),
            lines(true, components, true));
    }

    @Test
    void lockPostProcessingShowsLockedEvenIfTheMapIsNotLocked() {
        cache.update(MAP, (byte) 0, false);
        DataComponents components = components();
        components.put(DataComponentTypes.MAP_POST_PROCESSING, LOCK);
        assertEquals(List.of(grayKey("filled_map.locked")), lines(false, components, true));
    }

    @Test
    void customNameHidesTheIdLine() {
        cache.update(MAP, (byte) 0, false);
        DataComponents components = components();
        components.put(DataComponentTypes.CUSTOM_NAME, Component.text("Treasure"));
        assertEquals(List.of(), lines(false, components, true));
    }

    @Test
    void aMapTheServerNeverDescribedIsUnknown() {
        assertEquals(List.of(grayKey("filled_map.unknown")), lines(true, components(), true));
    }

    @Test
    void withoutASessionTheMapIsUnknown() {
        assertEquals(List.of(grayKey("filled_map.unknown")), lines(false, components(), false));
    }
}
