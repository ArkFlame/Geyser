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

package org.geysermc.geyser.registry.loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.geysermc.geyser.registry.type.GeyserMappingItem;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemMappingOverridesTest {
    @Test
    public void appliesShippedForkOverridesOverSubmoduleMappings() throws IOException {
        Map<String, GeyserMappingItem> items = new HashMap<>();
        items.put("minecraft:furnace_minecart", new GeyserMappingItem("minecraft:hopper_minecart", 0, null, null, true));
        items.put("minecraft:copper_bars", new GeyserMappingItem("minecraft:copper_bars", 0, 100, 101, false));

        assertEquals(2, ItemMappingOverrides.apply(items, shippedOverrides()));

        GeyserMappingItem furnace = items.get("minecraft:furnace_minecart");
        assertEquals("minecraft:hopper_minecart", furnace.getBedrockIdentifier());
        assertEquals(1, furnace.getBedrockData());
        assertTrue(furnace.isEntityPlacer());

        GeyserMappingItem copperBars = items.get("minecraft:copper_bars");
        assertEquals("minecraft:copper_bars", copperBars.getBedrockIdentifier());
        assertEquals(Integer.valueOf(9724), copperBars.getFirstBlockRuntimeId());
        assertEquals(Integer.valueOf(9755), copperBars.getLastBlockRuntimeId());
    }

    @Test
    public void ignoresMetadataAndUnmappedKeys() {
        JsonObject overrides = new JsonObject();
        overrides.addProperty("_comment", "metadata");
        JsonObject entry = new JsonObject();
        entry.addProperty("bedrock_data", 3);
        overrides.add("minecraft:furnace_minecart", entry);
        overrides.add("minecraft:missing_item", entry);

        Map<String, GeyserMappingItem> items = new HashMap<>();
        items.put("minecraft:furnace_minecart", new GeyserMappingItem("minecraft:hopper_minecart", 0, null, null, true));

        assertEquals(1, ItemMappingOverrides.apply(items, overrides));
        assertEquals(3, items.get("minecraft:furnace_minecart").getBedrockData());
    }

    private static JsonObject shippedOverrides() throws IOException {
        try (InputStream stream = ItemMappingOverridesTest.class.getClassLoader()
                .getResourceAsStream(ItemMappingOverrides.OVERRIDES_RESOURCE);
             InputStreamReader reader = new InputStreamReader(stream)) {
            //noinspection deprecation
            return new JsonParser().parse(reader).getAsJsonObject();
        }
    }
}
