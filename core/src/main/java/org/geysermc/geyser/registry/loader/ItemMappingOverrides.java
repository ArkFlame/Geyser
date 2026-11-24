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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.geysermc.geyser.GeyserBootstrap;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.registry.type.GeyserMappingItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Applies fork-owned fixes on top of the item mappings shipped by the pinned GeyserMC/mappings submodule.
 */
public final class ItemMappingOverrides {
    public static final String OVERRIDES_RESOURCE = "item_mapping_overrides.json";

    private ItemMappingOverrides() {
    }

    /**
     * Loads and merges the overrides into {@code items}. Missing keys are ignored so an upstream mapping update that
     * removes an entry cannot make the fork fail at startup.
     */
    public static void apply(GeyserBootstrap bootstrap, Map<String, GeyserMappingItem> items) {
        InputStream overrideStream = bootstrap.getResourceOrNull(OVERRIDES_RESOURCE);
        if (overrideStream == null) {
            return;
        }

        try (InputStream stream = overrideStream;
             InputStreamReader isr = new InputStreamReader(stream)) {
            //noinspection deprecation
            JsonObject overrides = new JsonParser().parse(isr).getAsJsonObject();
            int applied = apply(items, overrides);
            if (applied > 0) {
                GeyserImpl.getInstance().getLogger().debug("Applied " + applied + " item mapping override(s) from " + OVERRIDES_RESOURCE);
            }
        } catch (IOException e) {
            throw new AssertionError("Unable to load item mapping overrides", e);
        }
    }

    static int apply(Map<String, GeyserMappingItem> items, JsonObject overrides) {
        int applied = 0;
        for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
            if (entry.getKey().startsWith("_")) {
                continue;
            }

            GeyserMappingItem base = items.get(entry.getKey());
            if (base == null) {
                continue;
            }

            items.put(entry.getKey(), merge(base, entry.getValue().getAsJsonObject()));
            applied++;
        }
        return applied;
    }

    private static GeyserMappingItem merge(GeyserMappingItem base, JsonObject override) {
        GeyserMappingItem item = base;
        if (override.has("bedrock_identifier")) {
            item = item.withBedrockIdentifier(override.get("bedrock_identifier").getAsString());
        }
        if (override.has("bedrock_data")) {
            item = item.withBedrockData(override.get("bedrock_data").getAsInt());
        }
        if (override.has("firstBlockRuntimeId")) {
            item = item.withFirstBlockRuntimeId(override.get("firstBlockRuntimeId").getAsInt());
        }
        if (override.has("lastBlockRuntimeId")) {
            item = item.withLastBlockRuntimeId(override.get("lastBlockRuntimeId").getAsInt());
        }
        if (override.has("is_entity_placer")) {
            item = item.withEntityPlacer(override.get("is_entity_placer").getAsBoolean());
        }
        return item;
    }
}
