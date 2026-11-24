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

package org.geysermc.geyser.registry.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.registry.type.SoundMapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads sounds from the given input.
 */
public class SoundRegistryLoader implements RegistryLoader<String, Map<String, SoundMapping>> {
    private static final String OVERRIDES_RESOURCE = "sound_mapping_overrides.json";

    @Override
    public Map<String, SoundMapping> load(String input) {
        JsonObject soundsJson;
        try (InputStream stream = GeyserImpl.getInstance().getBootstrap().getResourceOrThrow(input);
              InputStreamReader isr = new InputStreamReader(stream)) {
            //noinspection deprecation
            soundsJson = new JsonParser().parse(isr).getAsJsonObject();
        } catch (IOException e) {
            throw new AssertionError("Unable to load sound mappings", e);
        }

        Map<String, SoundMapping> soundMappings = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : soundsJson.entrySet()) {
            JsonObject brMap = entry.getValue().getAsJsonObject();
            String javaSound = entry.getKey();
            soundMappings.put(javaSound, new SoundMapping(
                            javaSound,
                            brMap.has("bedrock_mapping") ? brMap.get("bedrock_mapping").getAsString() : null,
                            brMap.has("playsound_mapping") ? brMap.get("playsound_mapping").getAsString() : null,
                            brMap.has("integrated_playsound_mapping") ? brMap.get("integrated_playsound_mapping").getAsString() : null,
                            brMap.has("extra_data") ? brMap.get("extra_data").getAsInt() : -1,
                            brMap.has("identifier") ? brMap.get("identifier").getAsString() : null,
                            brMap.has("level_event") && brMap.get("level_event").getAsBoolean(),
                            brMap.has("pitch_adjust") ? brMap.get("pitch_adjust").getAsFloat() : 1.0f
                    )
            );
        }

        applyOverrides(soundMappings);
        return soundMappings;
    }

    /**
     * Merges fork-owned overrides over the mappings loaded from the GeyserMC/mappings submodule. The overrides live in
     * their own resource so that the submodule can stay on its pinned upstream commit. Keys starting with an underscore
     * are metadata and are ignored.
     */
    private static void applyOverrides(Map<String, SoundMapping> soundMappings) {
        InputStream overrideStream = GeyserImpl.getInstance().getBootstrap().getResourceOrNull(OVERRIDES_RESOURCE);
        if (overrideStream == null) {
            return;
        }

        try (InputStream stream = overrideStream;
             InputStreamReader isr = new InputStreamReader(stream)) {
            //noinspection deprecation
            JsonObject overrides = new JsonParser().parse(isr).getAsJsonObject();
            int applied = 0;
            for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                if (entry.getKey().startsWith("_")) {
                    continue;
                }

                SoundMapping base = soundMappings.get(entry.getKey());
                if (base == null) {
                    continue;
                }

                soundMappings.put(entry.getKey(), merge(base, entry.getValue().getAsJsonObject()));
                applied++;
            }

            if (applied > 0) {
                GeyserImpl.getInstance().getLogger().debug("Applied " + applied + " sound mapping override(s) from " + OVERRIDES_RESOURCE);
            }
        } catch (IOException e) {
            throw new AssertionError("Unable to load sound mapping overrides", e);
        }
    }

    private static SoundMapping merge(SoundMapping base, JsonObject override) {
        return new SoundMapping(
                base.java(),
                override.has("bedrock_mapping") ? override.get("bedrock_mapping").getAsString() : base.bedrock(),
                override.has("playsound_mapping") ? override.get("playsound_mapping").getAsString() : base.playsound(),
                override.has("integrated_playsound_mapping") ? override.get("integrated_playsound_mapping").getAsString() : base.integratedPlaySound(),
                override.has("extra_data") ? override.get("extra_data").getAsInt() : base.extraData(),
                override.has("identifier") ? override.get("identifier").getAsString() : base.identifier(),
                override.has("level_event") ? override.get("level_event").getAsBoolean() : base.levelEvent(),
                override.has("pitch_adjust") ? override.get("pitch_adjust").getAsFloat() : base.pitchAdjust()
        );
    }
}
