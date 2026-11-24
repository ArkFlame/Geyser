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

package org.geysermc.geyser.translator.item;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TooltipStyleMarkersTest {

    // Worked out independently of this class (Python zlib.crc32), so a resource pack built from the same rule matches
    @Test
    void markerIsTheCrcOfTheStyleId() {
        assertEquals("\uEF00\uEF73\uEFB8\uEFE8", TooltipStyleMarkers.marker(Key.key("minecraft:epic")));
        assertEquals("\uEF00\uEF60\uEFA7\uEFDB", TooltipStyleMarkers.marker(Key.key("minecraft:juggernaut_animated")));
        assertEquals("\uEF00\uEF7A\uEF9D\uEFF7", TooltipStyleMarkers.marker(Key.key("minecraft:legendary")));
        assertEquals("\uEF00\uEF71\uEF8F\uEFCD", TooltipStyleMarkers.marker(Key.key("minecraft:common")));
    }

    @Test
    void optionOffLeavesTheBedrockNameUnchanged() {
        String name = "\u00a7r\u00a7cExcalibur";
        // Same string instance contents: the default path must stay byte-identical
        assertEquals(name, TooltipStyleMarkers.append(name, Key.key("minecraft:epic"), false));
        assertEquals("", TooltipStyleMarkers.append("", Key.key("minecraft:epic"), false));
        assertNull(TooltipStyleMarkers.append(null, Key.key("minecraft:epic"), false));
        assertNull(TooltipStyleMarkers.append(null, null, false));
    }

    @Test
    void optionOnAppendsTheMarker() {
        String name = "\u00a7r\u00a7cExcalibur";
        assertEquals(name + "\uEF00\uEF73\uEFB8\uEFE8",
            TooltipStyleMarkers.append(name, Key.key("minecraft:epic"), true));
        // No tooltip style means nothing to mark, even with the option on
        assertEquals(name, TooltipStyleMarkers.append(name, null, true));
        assertNull(TooltipStyleMarkers.append(null, null, true));
        // Items differing only by tooltip style keep different names but stack per item, not per marker
        assertEquals(TooltipStyleMarkers.marker(Key.key("minecraft:epic")),
            TooltipStyleMarkers.marker(Key.key("minecraft:epic")));
    }

    @Test
    void stripRemovesMarkerCharactersOnly() {
        String name = "\u00a7r\u00a76Spooky Key" + TooltipStyleMarkers.marker(Key.key("minecraft:spooky_animated"));
        assertEquals("\u00a7r\u00a76Spooky Key", TooltipStyleMarkers.strip(name));
        // an anvil rename that deleted the marker's first character along with the old name
        assertEquals("nogard", TooltipStyleMarkers.strip("nogard\uEF60\uEFA7\uEFDB"));
        // other private use characters, such as a server's own font glyphs, stay
        assertEquals("\uE001 Key\uEF01", TooltipStyleMarkers.strip("\uE001 Key\uEF01"));
    }
}
