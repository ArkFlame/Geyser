package org.geysermc.geyser.util;

import org.geysermc.mcprotocollib.protocol.data.game.chunk.BitStorage;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BitStorageUnpackerTest {

    @Test
    void matchesGetForEveryBitWidthAndRandomContents() {
        Random random = new Random(11);
        for (int bits = 1; bits <= 31; bits++) {
            for (int size : new int[]{64, 1000, 4096}) {
                BitStorage storage = new BitStorage(bits, size);
                long max = (1L << bits) - 1L;
                for (int i = 0; i < size; i++) {
                    storage.set(i, (int) (random.nextLong() & max));
                }
                int[] out = new int[size];
                BitStorageUnpacker.unpack(storage, out);
                for (int i = 0; i < size; i++) {
                    assertEquals(storage.get(i), out[i], "bits=" + bits + " size=" + size + " index=" + i);
                }
            }
        }
    }

    @Test
    void matchesGetForShortOrPaddedBackingArray() {
        // The constructor pads or truncates a wrong-length array (seen on some servers); the result must still match get()
        long[] data = new long[3];
        data[0] = 0x0123456789ABCDEFL;
        data[1] = -1L;
        BitStorage storage = new BitStorage(5, 4096, data);
        int[] out = new int[4096];
        BitStorageUnpacker.unpack(storage, out);
        for (int i = 0; i < 4096; i++) {
            assertEquals(storage.get(i), out[i], "index=" + i);
        }
    }

    @Test
    void cheaperThanGetLoop() {
        Random random = new Random(5);
        int[] widths = {4, 5, 8, 15};
        int sink = 0;
        long bestGet = Long.MAX_VALUE, bestUnpack = Long.MAX_VALUE;
        BitStorage[] storages = new BitStorage[widths.length];
        for (int w = 0; w < widths.length; w++) {
            storages[w] = new BitStorage(widths[w], 4096);
            long max = (1L << widths[w]) - 1L;
            for (int i = 0; i < 4096; i++) storages[w].set(i, (int) (random.nextLong() & max));
        }
        int[] out = new int[4096];
        for (int warm = 0; warm < 3000; warm++) {
            for (BitStorage s : storages) {
                for (int i = 0; i < 4096; i++) sink += s.get(i);
                BitStorageUnpacker.unpack(s, out);
                sink += out[4095];
            }
        }
        for (int round = 0; round < 7; round++) {
            long t0 = System.nanoTime();
            for (int rep = 0; rep < 500; rep++) {
                for (BitStorage s : storages) {
                    for (int i = 0; i < 4096; i++) out[i] = s.get(i);
                    sink += out[4095];
                }
            }
            bestGet = Math.min(bestGet, System.nanoTime() - t0);
            t0 = System.nanoTime();
            for (int rep = 0; rep < 500; rep++) {
                for (BitStorage s : storages) {
                    BitStorageUnpacker.unpack(s, out);
                    sink += out[4095];
                }
            }
            bestUnpack = Math.min(bestUnpack, System.nanoTime() - t0);
        }
        double perSectionGet = (double) bestGet / (500 * widths.length);
        double perSectionUnpack = (double) bestUnpack / (500 * widths.length);
        System.out.println("PERF section decode ns/section get-loop=" + (long) perSectionGet + " unpack=" + (long) perSectionUnpack
            + " cpu-ratio=" + perSectionGet / perSectionUnpack + " sink=" + sink);
        assertTrue(bestUnpack < bestGet, "unpack=" + bestUnpack + " get=" + bestGet);
    }
}
