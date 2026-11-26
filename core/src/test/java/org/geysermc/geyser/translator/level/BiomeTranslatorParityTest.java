package org.geysermc.geyser.translator.level;

import org.geysermc.geyser.level.chunk.BlockStorage;
import org.geysermc.geyser.registry.java.JavaRegistries;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.chunk.BitStorage;
import org.geysermc.mcprotocollib.protocol.data.game.chunk.DataPalette;
import org.geysermc.mcprotocollib.protocol.data.game.chunk.palette.PaletteType;
import org.geysermc.mcprotocollib.protocol.data.game.chunk.palette.GlobalPalette;
import org.geysermc.mcprotocollib.protocol.data.game.chunk.palette.ListPalette;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BiomeTranslatorParityTest {
    private static final int REGISTRY = 64;

    private static int bedrockOf(int javaId) {
        return 1000 + javaId * 7;
    }

    private static GeyserSession session() {
        GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
        when(session.javaRegistries().registry(JavaRegistries.BIOME).get(anyInt()))
            .thenAnswer(inv -> Optional.of(bedrockOf(inv.getArgument(0))));
        return session;
    }

    // Builds a biome section in the wire layout: entry i is y*16 + z*4 + x
    private static DataPalette randomBiomes(Random random, int distinct, int zeroBias, boolean global) {
        int bits = global ? 6 : Math.max(1, 32 - Integer.numberOfLeadingZeros(distinct - 1));
        BitStorage storage = new BitStorage(bits, 64);
        org.geysermc.mcprotocollib.protocol.data.game.chunk.palette.Palette palette;
        if (global) {
            palette = new GlobalPalette();
        } else {
            palette = new ListPalette(bits);
            for (int id = 0; id < distinct; id++) palette.stateToId(id + 3);
        }
        for (int i = 0; i < 64; i++) {
            storage.set(i, random.nextInt(100) < zeroBias ? 0 : random.nextInt(distinct));
        }
        return DataPalette.create(palette, storage, PaletteType.BIOME, REGISTRY);
    }

    private static void assertMatchesPerCell(DataPalette biomes, BlockStorage storage) {
        for (int bx = 0; bx < 16; bx++) for (int bz = 0; bz < 16; bz++) for (int by = 0; by < 16; by++) {
            int i = ((by >> 2) << 4) | ((bz >> 2) << 2) | (bx >> 2);
            int javaId = biomes.getPalette().idToState(biomes.getStorage().get(i));
            assertEquals(bedrockOf(javaId), storage.getFullBlock((bx << 8) | (bz << 4) | by), "cell " + bx + "," + by + "," + bz);
        }
    }

    @Test
    void everyCellMatchesItsBiomeAcrossPaletteShapes() {
        GeyserSession session = session();
        Random random = new Random(3);
        for (boolean global : new boolean[]{false, true}) {
            for (int distinct : new int[]{1, 2, 3, 5, 9, 20, 40}) {
                for (int zeroBias : new int[]{0, 50, 95}) {
                    for (int rep = 0; rep < 8; rep++) {
                        DataPalette biomes = randomBiomes(random, distinct, zeroBias, global);
                        assertMatchesPerCell(biomes, BiomeTranslator.toNewBedrockBiome(session, biomes));
                    }
                }
            }
        }
    }

    @Test
    void timing() {
        GeyserSession session = session();
        Random random = new Random(9);
        DataPalette[] set = new DataPalette[16];
        for (int i = 0; i < set.length; i++) set[i] = randomBiomes(random, 4, 70, false);
        long sink = 0;
        for (int w = 0; w < 4000; w++) for (DataPalette p : set) sink += BiomeTranslator.toNewBedrockBiome(session, p).getFullBlock(5);
        long best = Long.MAX_VALUE;
        for (int r = 0; r < 7; r++) {
            long t0 = System.nanoTime();
            for (int rep = 0; rep < 500; rep++) for (DataPalette p : set) sink += BiomeTranslator.toNewBedrockBiome(session, p).getFullBlock(5);
            best = Math.min(best, System.nanoTime() - t0);
        }
        System.out.println("PERF biome section ns=" + best / (500 * set.length) + " sink=" + sink);
    }
}
