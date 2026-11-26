/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
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

package org.geysermc.geyser.translator.protocol.java.level;

import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.BlockChangeEntry.MessageType;
import org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateSubChunkBlocksPacket;
import org.geysermc.geyser.entity.type.ItemFrameEntity;
import org.geysermc.geyser.level.block.Blocks;
import org.geysermc.geyser.level.block.type.Block;
import org.geysermc.geyser.level.block.type.BlockState;
import org.geysermc.geyser.registry.BlockRegistries;
import org.geysermc.mcprotocollib.protocol.data.game.chunk.DataPalette;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.BlockChangeEntry;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundSectionBlocksUpdatePacket;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.protocol.PacketTranslator;
import org.geysermc.geyser.translator.protocol.Translator;

import java.util.BitSet;

@Translator(packet = ClientboundSectionBlocksUpdatePacket.class)
public class JavaSectionBlocksUpdateTranslator extends PacketTranslator<ClientboundSectionBlocksUpdatePacket> {

    private static final int FLAG_ALL = 1 << UpdateBlockPacket.Flag.NEIGHBORS.ordinal() | 1 << UpdateBlockPacket.Flag.NETWORK.ordinal();

    @Override
    public void translate(GeyserSession session, ClientboundSectionBlocksUpdatePacket packet) {
        DataPalette palette = null;
        if (session.getChunkCache().isCache()) {
            palette = session.getChunkCache().getChunkSection(packet.getChunkX(), packet.getChunkY(), packet.getChunkZ(), true);
            if (palette == null) {
                return;
            }
        }

        Vector3i clientBreakPos = session.getBlockBreakHandler().getCurrentBlockPos();
        BitSet waterlogged = BlockRegistries.WATERLOGGED.get();

        UpdateSubChunkBlocksPacket updateSubChunkBlocksPacket = new UpdateSubChunkBlocksPacket();
        updateSubChunkBlocksPacket.setPosition(Vector3i.from(
            packet.getChunkX() << 4, packet.getChunkY() << 4, packet.getChunkZ() << 4
        ));

        final var worldCache = session.getWorldCache();
        final var blockMappings = session.getBlockMappings();
        for (BlockChangeEntry entry : packet.getEntries()) {
            final Vector3i entryPos = entry.getPosition();
            worldCache.removePrediction(entryPos);

            // Hack to avoid looking up blockstates for the currently broken position each tick
            if (clientBreakPos != null && clientBreakPos.equals(entryPos)) {
                session.getBlockBreakHandler().setUpdatedServerBlockStateId(entry.getBlock());
            }

            int oldBlock;
            if (palette != null) {
                oldBlock = palette.get(entryPos.getX() & 0xF, entryPos.getY() & 0xF, entryPos.getZ() & 0xF);
                if (entry.getBlock() == oldBlock) {
                    // Skip unchanged blocks which may occur with older versions of Minecraft
                    continue;
                }

                palette.set(entryPos.getX() & 0xF, entryPos.getY() & 0xF, entryPos.getZ() & 0xF, entry.getBlock());
            } else {
                oldBlock = -1;
            }

            BlockState blockState = BlockState.of(entry.getBlock());
            if (blockState.is(Blocks.AIR)) {
                ItemFrameEntity itemFrameEntity = ItemFrameEntity.getItemFrameEntity(session, entryPos);
                if (itemFrameEntity != null) { // Item frame is still present and no block overrides that; refresh it
                    itemFrameEntity.updateBlock(blockState, true);
                    continue;
                }
            }

            // Some block may have special handling, keep it that way
            if (!(blockState.block().getClass() == Block.class)) {
                blockState.block().updateBlock(session, blockState, entryPos);
                continue;
            }

            // Skull is gone
            session.getSkullCache().removeSkull(entryPos);

            updateSubChunkBlocksPacket.getStandardBlocks().add(new org.cloudburstmc.protocol.bedrock.data.BlockChangeEntry(
                entryPos,
                blockMappings.getBedrockBlock(blockState),
                FLAG_ALL,
                -1,
                MessageType.NONE
            ));

            boolean isWaterlogged = waterlogged.get(entry.getBlock());
            if (palette == null || waterlogged.get(oldBlock) != isWaterlogged) {
                updateSubChunkBlocksPacket.getExtraBlocks().add(new org.cloudburstmc.protocol.bedrock.data.BlockChangeEntry(
                    entryPos,
                    isWaterlogged ? blockMappings.getBedrockWater() : blockMappings.getBedrockAir(),
                    0,
                    -1,
                    MessageType.NONE
                ));
            }
        }

        if (!updateSubChunkBlocksPacket.getStandardBlocks().isEmpty()) {
            session.sendUpstreamPacket(updateSubChunkBlocksPacket);
        }
    }
}
