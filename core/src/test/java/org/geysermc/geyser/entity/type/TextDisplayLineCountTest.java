package org.geysermc.geyser.entity.type;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextDisplayLineCountTest {
    @Test
    void matchesSplit() {
        String[] fixed = {"", "\n", "\n\n", "a", "a\n", "\na", "a\nb", "a\n\nb\n", "\n\n\nx\n\n"};
        for (String s : fixed) {
            assertEquals(s.split("\n", -1).length, TextDisplayEntity.countLines(s), s);
        }
        Random random = new Random(7);
        char[] alphabet = {'a', 'b', '\n', ' ', '\r'};
        for (int i = 0; i < 5000; i++) {
            char[] chars = new char[random.nextInt(40)];
            for (int j = 0; j < chars.length; j++) {
                chars[j] = alphabet[random.nextInt(alphabet.length)];
            }
            String s = new String(chars);
            assertEquals(s.split("\n", -1).length, TextDisplayEntity.countLines(s));
        }
    }
}
