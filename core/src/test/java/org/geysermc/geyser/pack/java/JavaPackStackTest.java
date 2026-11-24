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

package org.geysermc.geyser.pack.java;

import net.kyori.adventure.key.Key;
import org.geysermc.geyser.pack.java.contents.JavaPackComposed;
import org.geysermc.geyser.pack.java.contents.JavaPackContents;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaPackStackTest {

    private static final Key EN_US = Key.key("minecraft", "en_us");
    private static final Key DE_DE = Key.key("minecraft", "de_de");

    @Test
    void laterPacksOverrideEarlierOnes() {
        JavaPack first = pack(Map.of(EN_US, Map.of("key", "first", "only_first", "yes")));
        JavaPack second = pack(Map.of(EN_US, Map.of("key", "second")));

        JavaPackComposed composed = JavaPackStack.compose(List.of(first, second), "en_us");

        assertEquals("second", composed.flattenedTranslations().get("key"));
        assertEquals("yes", composed.flattenedTranslations().get("only_first"));
    }

    @Test
    void requestedLocaleOverridesEnglishBase() {
        JavaPack pack = pack(Map.of(
            EN_US, Map.of("key", "english"),
            DE_DE, Map.of("key", "german")
        ));

        JavaPackComposed enUs = JavaPackStack.compose(List.of(pack), "en_us");
        JavaPackComposed deDe = JavaPackStack.compose(List.of(pack), "de_de");

        assertEquals("english", enUs.flattenedTranslations().get("key"));
        assertEquals("german", deDe.flattenedTranslations().get("key"));
    }

    @Test
    void emptyStackComposesToEmptyTranslations() {
        assertTrue(JavaPackStack.compose(List.of(), "en_us").flattenedTranslations().isEmpty());
    }

    private static JavaPack pack(Map<Key, Map<String, String>> languages) {
        return new JavaPack(new JavaPack.Id(UUID.randomUUID(), null), new JavaPackContents(languages));
    }
}
