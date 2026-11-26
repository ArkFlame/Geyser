package org.geysermc.geyser.util;

import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Old-vs-new parity for the optimizations in the perf loop (see docs/OPTIMIZATION_LOG.md). */
class PerfParityTest {

    private static SoundEvent oldToSoundEvent(String sound) {
        try {
            return SoundEvent.valueOf(sound.toUpperCase(Locale.ROOT).replace('.', '_'));
        } catch (Exception ex) {
            return null;
        }
    }

    @Test
    void soundEventParity() {
        Random random = new Random(42);
        for (SoundEvent event : SoundEvent.values()) {
            String dotted = event.name().toLowerCase(Locale.ROOT).replace('_', '.');
            for (String s : List.of(event.name(), event.name().toLowerCase(Locale.ROOT), dotted)) {
                assertEquals(oldToSoundEvent(s), SoundUtils.toSoundEvent(s), s);
            }
        }
        String alphabet = "abcXYZ._-0123éİK ";
        for (int i = 0; i < 20000; i++) {
            StringBuilder sb = new StringBuilder();
            int len = random.nextInt(12);
            for (int j = 0; j < len; j++) {
                sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            assertEquals(oldToSoundEvent(sb.toString()), SoundUtils.toSoundEvent(sb.toString()), sb.toString());
        }
        assertEquals(null, SoundUtils.toSoundEvent(null));
        assertEquals(oldToSoundEvent(""), SoundUtils.toSoundEvent(""));
        assertEquals(oldToSoundEvent("item.armor.equip_generic"), SoundUtils.toSoundEvent("item.armor.equip_generic"));
    }

    @Test
    void soundEventAllocatesLessOnMiss() {
        com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long id = Thread.currentThread().getId();
        String miss = "minecraft.not.a.real.sound";
        for (int i = 0; i < 20000; i++) { // warm-up
            oldToSoundEvent(miss);
            SoundUtils.toSoundEvent(miss);
        }
        long a = mx.getThreadAllocatedBytes(id);
        for (int i = 0; i < 20000; i++) oldToSoundEvent(miss);
        long oldBytes = mx.getThreadAllocatedBytes(id) - a;
        a = mx.getThreadAllocatedBytes(id);
        for (int i = 0; i < 20000; i++) SoundUtils.toSoundEvent(miss);
        long newBytes = mx.getThreadAllocatedBytes(id) - a;
        System.out.println("PERF soundEvent miss alloc old=" + oldBytes + " new=" + newBytes);
        assertTrue(newBytes < oldBytes, "old=" + oldBytes + " new=" + newBytes);
    }

    @Test
    void inputDataLeftoverSetParity() {
        Random random = new Random(7);
        PlayerAuthInputData[] all = PlayerAuthInputData.values();
        for (int i = 0; i < 5000; i++) {
            Set<PlayerAuthInputData> input = EnumSet.noneOf(PlayerAuthInputData.class);
            for (PlayerAuthInputData d : all) if (random.nextInt(4) == 0) input.add(d);
            Set<PlayerAuthInputData> oldSet = new HashSet<>(input);
            Set<PlayerAuthInputData> newSet = EnumSet.noneOf(PlayerAuthInputData.class);
            newSet.addAll(input);
            for (PlayerAuthInputData d : input) {
                oldSet.remove(d);
                newSet.remove(d);
                for (PlayerAuthInputData probe : all) {
                    assertEquals(oldSet.contains(probe), newSet.contains(probe));
                }
            }
        }
    }
}
