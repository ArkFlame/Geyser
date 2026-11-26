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

package org.geysermc.geyser.network.bedrock;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.v2168.Bedrock_v2168;
import org.cloudburstmc.protocol.bedrock.data.EncodingSettings;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * #4609: a filled shulker box whose item NBT is a little over 100 KiB could not be placed, because the packet
 * failed to decode ("Read size exceeded limit: read=102997, limit=102400").
 */
class BedrockDecodeLimitsTest {
    private static final ItemDefinition SHULKER = new SimpleItemDefinition("minecraft:shulker_box", 1, false);

    private static BedrockCodecHelper helper() {
        BedrockCodecHelper helper = Bedrock_v2168.CODEC.createHelper();
        helper.setItemDefinitions(SimpleDefinitionRegistry.<ItemDefinition>builder().add(SHULKER).build());
        helper.setBlockDefinitions(SimpleDefinitionRegistry.<org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition>builder().build());
        return helper;
    }

    /** An item whose NBT is about {@code bytes} large, built from strings under the 64 KiB NBT string limit. */
    private static ItemData itemOfSize(int bytes) {
        NbtMapBuilder tag = NbtMap.builder();
        String chunk = "x".repeat(60000);
        for (int i = 0; i * 60000 < bytes; i++) {
            tag.putString("lore" + i, chunk);
        }
        return ItemData.builder().definition(SHULKER).count(1).tag(tag.build()).build();
    }

    private static ByteBuf encode(ItemData item) {
        BedrockCodecHelper writer = helper();
        writer.setEncodingSettings(EncodingSettings.UNLIMITED);
        ByteBuf buf = Unpooled.buffer();
        writer.writeItem(buf, item);
        return buf;
    }

    @Test
    void theDefaultHelperRejectsAnItemOverOneHundredKiB() {
        // Premise of the report: with untouched defaults this is not readable.
        ItemData item = itemOfSize(130_000);
        assertThrows(RuntimeException.class, () -> helper().readItem(encode(item)));
    }

    @Test
    void anItemOverOneHundredKiBIsReadOnceTheLimitsAreApplied() {
        ItemData item = itemOfSize(130_000);
        BedrockCodecHelper reader = helper();
        BedrockDecodeLimits.apply(reader);
        assertEquals(item.getTag(), reader.readItem(encode(item)).getTag());
    }

    @Test
    void theCapStillBoundsWhatAClientCanMakeUsAllocate() {
        BedrockCodecHelper reader = helper();
        BedrockDecodeLimits.apply(reader);
        ByteBuf tooBig = encode(itemOfSize(BedrockDecodeLimits.MAX_ITEM_NBT_SIZE + 200_000));
        assertThrows(RuntimeException.class, () -> reader.readItem(tooBig));
    }

    @Test
    void otherLimitsAreLeftAtTheirDefaults() {
        BedrockCodecHelper reader = helper();
        EncodingSettings before = reader.getEncodingSettings();
        BedrockDecodeLimits.apply(reader);
        assertEquals(before.maxNetworkNBTSize(), reader.getEncodingSettings().maxNetworkNBTSize());
        assertEquals(before.maxListSize(), reader.getEncodingSettings().maxListSize());
        assertEquals(BedrockDecodeLimits.MAX_ITEM_NBT_SIZE, reader.getEncodingSettings().maxItemNBTSize());
    }
}
