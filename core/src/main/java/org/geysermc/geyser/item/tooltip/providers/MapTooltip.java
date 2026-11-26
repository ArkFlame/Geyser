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

package org.geysermc.geyser.item.tooltip.providers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.geyser.item.tooltip.ComponentTooltipProvider;
import org.geysermc.geyser.item.tooltip.TooltipContext;
import org.geysermc.geyser.session.cache.MapCache;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;

import java.util.function.Consumer;

/**
 * The map lines Java adds, as in vanilla {@code MapId#addToTooltip}. The zoom level and locked flag come from
 * the last map packet the server sent, remembered in {@link MapCache}.
 */
public class MapTooltip implements ComponentTooltipProvider<Integer> {
    private static final int MAX_SCALE = 4;
    private static final int POST_PROCESSING_LOCK = 0;
    private static final int POST_PROCESSING_SCALE = 1;

    @Override
    public void addTooltip(TooltipContext context, Consumer<Component> adder, @NonNull Integer component) {
        MapCache.MapInfo info = context.session().map(session -> session.getMapCache().get(component)).orElse(null);
        if (info == null) {
            // Java says this while it has not been told about the map
            adder.accept(Component.translatable("filled_map.unknown").color(NamedTextColor.GRAY));
            return;
        }

        Integer postProcessing = context.components().get(DataComponentTypes.MAP_POST_PROCESSING);
        if (context.components().get(DataComponentTypes.CUSTOM_NAME) == null && postProcessing == null) {
            adder.accept(Component.translatable("filled_map.id", Component.text(component)).color(NamedTextColor.GRAY));
        }
        if (info.locked() || (postProcessing != null && postProcessing == POST_PROCESSING_LOCK)) {
            adder.accept(Component.translatable("filled_map.locked").color(NamedTextColor.GRAY));
        }
        if (context.advanced()) {
            int scaleToAdd = postProcessing != null && postProcessing == POST_PROCESSING_SCALE ? 1 : 0;
            int scale = Math.min(info.scale() + scaleToAdd, MAX_SCALE);
            adder.accept(Component.translatable("filled_map.scale", Component.text(1 << scale)).color(NamedTextColor.GRAY));
            adder.accept(Component.translatable("filled_map.level", Component.text(scale), Component.text(MAX_SCALE))
                .color(NamedTextColor.GRAY));
        }
    }
}
