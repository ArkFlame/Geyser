package org.geysermc.geyser.network.translators.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.geysermc.geyser.translator.text.MessageTranslator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** The hoisted constant must equal the string the old code concatenated on every call. */
class NoColorMessageParityTest {

    @Test
    void constantEqualsOldConcatenation() throws Exception {
        Field f = MessageTranslator.class.getDeclaredField("SECTION_WHITE");
        f.setAccessible(true);
        String old = "" + LegacyComponentSerializer.SECTION_CHAR + net.kyori.adventure.text.serializer.legacy.CharacterAndFormat.WHITE.character();
        assertEquals(old, f.get(null));
        assertEquals("§f", f.get(null));
    }

    @Test
    void noColourOutputHasNoWhiteCodes() {
        Component c = Component.text("a").append(Component.text("b").color(NamedTextColor.WHITE)).append(Component.text("c"));
        String out = MessageTranslator.convertNoColorMessage(c, "en_us");
        assertFalse(out.contains("§f"));
        assertEquals("abc", out.replace("§r", ""));
    }
}
