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

import it.unimi.dsi.fastutil.ints.IntList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.geyser.item.tooltip.ComponentTooltipProvider;
import org.geysermc.geyser.item.tooltip.TooltipContext;
import org.geysermc.geyser.registry.java.BuiltInJavaRegistries;
import org.geysermc.geyser.registry.java.JavaRegistries;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.AdventureModePredicate;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.AdventureModePredicate.BlockPredicate;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.HolderSet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Java's {@code can_break} and {@code can_place_on} tooltip lines. Bedrock's native fields are kept
 * in place for adventure-mode handling; this provider only adds the Java-styled tooltip text when
 * the opt-in provider path is enabled.
 */
public class AdventureModeTooltip implements ComponentTooltipProvider<AdventureModePredicate> {
    private final String titleKey;

    public AdventureModeTooltip(String titleKey) {
        this.titleKey = titleKey;
    }

    @Override
    public void addTooltip(TooltipContext context, Consumer<Component> adder, @NonNull AdventureModePredicate component) {
        List<String> names = new ArrayList<>();
        for (BlockPredicate predicate : component.getPredicates()) {
            HolderSet blocks = predicate.getBlocks();
            if (blocks == null) {
                continue;
            }
            IntList holders = blocks.getHolders();
            if (holders != null) {
                for (int blockId : holders) {
                    String name = resolveBlock(context, blockId);
                    if (name != null) {
                        names.add(name);
                    }
                }
            } else if (blocks.getLocation() != null) {
                names.add("#" + blocks.getLocation().asString());
            }
        }
        if (!names.isEmpty()) {
            adder.accept(Component.translatable(titleKey)
                .append(Component.text(" " + String.join(", ", names)))
                .color(NamedTextColor.GRAY));
        }
    }

    private static String resolveBlock(TooltipContext context, int blockId) {
        return context.session()
            .flatMap(session -> JavaRegistries.BLOCK.get(session.javaRegistries(), blockId))
            .or(() -> BuiltInJavaRegistries.BLOCK.get(blockId))
            .map(block -> block.javaIdentifier().asString())
            .orElse(null);
    }
}
