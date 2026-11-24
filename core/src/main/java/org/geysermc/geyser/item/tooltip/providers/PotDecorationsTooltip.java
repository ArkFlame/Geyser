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
import org.geysermc.geyser.inventory.GeyserItemStack;
import org.geysermc.geyser.item.Items;
import org.geysermc.geyser.item.tooltip.ComponentTooltipProvider;
import org.geysermc.geyser.item.tooltip.TooltipContext;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.PotDecorations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PotDecorationsTooltip implements ComponentTooltipProvider<PotDecorations> {

    @Override
    public void addTooltip(TooltipContext context, Consumer<Component> adder, @NonNull PotDecorations decorations) {
        context.withSession(session -> {
            List<Component> tooltip = new ArrayList<>();
            boolean allBrick = true;
            for (ItemStack stack : List.of(decorations.back(), decorations.left(), decorations.right(), decorations.front())) {
                if (stack == null || stack.getId() == 0) {
                    continue;
                }
                if (stack.getId() != Items.BRICK.javaId()) {
                    allBrick = false;
                }
                tooltip.add(GeyserItemStack.from(session, stack).getName().colorIfAbsent(NamedTextColor.GRAY));
            }
            if (!allBrick && !tooltip.isEmpty()) {
                adder.accept(Component.empty());
                tooltip.forEach(adder);
            }
        });
    }
}
