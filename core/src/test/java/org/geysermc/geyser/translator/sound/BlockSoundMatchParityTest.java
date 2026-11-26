package org.geysermc.geyser.translator.sound;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockSoundMatchParityTest {
    // The previous inline loop, with the state string rebuilt by the caller per identifier
    private static boolean old(String[] ids, String state) {
        for (String id : ids) {
            if (state.contains(id)) {
                return true;
            }
        }
        return false;
    }

    @Test
    void sameAnswerAsInlineLoop() {
        String[] states = {"minecraft:stone", "minecraft:oak_door[facing=north,open=true]", "minecraft:cake[bites=3]", ""};
        String[][] sets = {{}, {"door"}, {"cake", "door"}, {"stone", "x"}, {"nothing"}, {""}};
        for (String s : states) {
            for (String[] ids : sets) {
                assertEquals(old(ids, s), BlockSoundInteractionTranslator.anyContained(s, ids), s);
            }
        }
        Random random = new Random(11);
        String alphabet = "ab:_[]=,";
        for (int i = 0; i < 3000; i++) {
            String s = random(random, alphabet, 30);
            String[] ids = new String[random.nextInt(4)];
            for (int j = 0; j < ids.length; j++) {
                ids[j] = random(random, alphabet, 3);
            }
            assertEquals(old(ids, s), BlockSoundInteractionTranslator.anyContained(s, ids));
        }
    }

    private static String random(Random random, String alphabet, int max) {
        char[] c = new char[random.nextInt(max)];
        for (int i = 0; i < c.length; i++) {
            c[i] = alphabet.charAt(random.nextInt(alphabet.length()));
        }
        return new String(c);
    }
}
