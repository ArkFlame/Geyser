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

package org.geysermc.geyser.session.cache;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.inventory.GeyserItemStack;
import org.geysermc.geyser.inventory.PlayerInventory;
import org.geysermc.geyser.level.WorldManager;
import org.geysermc.geyser.level.block.Blocks;
import org.geysermc.geyser.level.block.type.BlockState;
import org.geysermc.geyser.level.physics.Direction;
import org.geysermc.geyser.registry.type.BlockMappings;
import org.geysermc.geyser.registry.type.GeyserBedrockBlock;
import org.geysermc.geyser.registry.type.ItemMapping;
import org.geysermc.geyser.registry.type.ItemMappings;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerAction;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundPlayerActionPacket;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regression test for GeyserMC/Geyser#2023 / #875 / #906: extinguishing fire
 * must not also mine the block the Bedrock client actually targeted.
 */
class FireBlockBreakHandlerTest {

    /** Pins the real (item/tool/tick dependent) break speed so the test stays in the slow-break branch. */
    private static final class TestHandler extends BlockBreakHandler {
        private final float progress;

        TestHandler(GeyserSession session, float progress) {
            super(session);
            this.progress = progress;
        }

        @Override
        protected float calculateBreakProgress(BlockState state, Vector3i vector, GeyserItemStack stack) {
            return progress;
        }
    }

    @Test
    void extinguishingFireMustNotMineTheBlockBehindIt() {
        GeyserSession session = mock(GeyserSession.class);
        GeyserImpl geyser = mock(GeyserImpl.class);
        WorldManager worldManager = mock(WorldManager.class);
        BlockMappings blockMappings = mock(BlockMappings.class);
        ItemMappings itemMappings = mock(ItemMappings.class);
        SkullCache skullCache = mock(SkullCache.class);
        WorldCache worldCache = mock(WorldCache.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        GeyserItemStack item = mock(GeyserItemStack.class);
        ItemMapping mapping = mock(ItemMapping.class);
        BlockState belowState = mock(BlockState.class);
        BlockState fireState = mock(BlockState.class);
        GeyserBedrockBlock bedrockBlock = mock(GeyserBedrockBlock.class);

        // Bedrock raycasts through the fire and targets the block below it (face UP).
        Vector3i below = Vector3i.from(0, 64, 0);
        Vector3i firePos = Vector3i.from(0, 65, 0);

        when(session.getGeyser()).thenReturn(geyser);
        when(geyser.getWorldManager()).thenReturn(worldManager);
        when(worldManager.blockAt(session, firePos)).thenReturn(fireState);
        when(fireState.block()).thenReturn(Blocks.FIRE);

        when(session.getWorldCache()).thenReturn(worldCache);
        when(worldCache.nextPredictionSequence()).thenReturn(0);

        when(session.getPlayerInventory()).thenReturn(inventory);
        when(inventory.getItemInHand()).thenReturn(item);
        when(item.getMapping(session)).thenReturn(mapping);
        when(item.has(DataComponentTypes.TOOL)).thenReturn(false);
        when(item.getJavaId()).thenReturn(0);

        when(session.getBlockMappings()).thenReturn(blockMappings);
        when(blockMappings.getBedrockBlock(belowState)).thenReturn(bedrockBlock);
        when(bedrockBlock.getRuntimeId()).thenReturn(0);

        when(session.getItemMappings()).thenReturn(itemMappings);
        when(itemMappings.getNonVanillaCustomItemIds()).thenReturn(new IntOpenHashSet());
        when(session.getSkullCache()).thenReturn(skullCache);
        when(skullCache.getSkulls()).thenReturn(Collections.emptyMap());

        when(session.isInstabuild()).thenReturn(false);
        when(session.isFlying()).thenReturn(false);

        new TestHandler(session, 0.1f).handleStartBreak(below, belowState, Direction.UP, 0L);

        ArgumentCaptor<ServerboundPlayerActionPacket> captor =
            ArgumentCaptor.forClass(ServerboundPlayerActionPacket.class);
        verify(session, atLeastOnce()).sendDownstreamGamePacket(captor.capture());
        List<ServerboundPlayerActionPacket> packets = captor.getAllValues();

        assertTrue(packets.stream().anyMatch(p ->
                p.getAction() == PlayerAction.START_DIGGING && firePos.equals(p.getPosition())),
            "fire must be sent to Java as START_DIGGING so it is extinguished server-side");
        assertTrue(packets.stream().noneMatch(p -> below.equals(p.getPosition())),
            "the block behind the fire must not be mined (GeyserMC/Geyser#906)");
    }
}
