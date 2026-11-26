package org.geysermc.geyser.level.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.protocol.common.util.VarInts;
import org.geysermc.geyser.level.chunk.bitarray.BitArrayVersion;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pre-sizing the buffer must not change a single byte; it only avoids repeated regrowth. */
class ChunkWritePresizeParityTest {

    private static void oldStorageWrite(BlockStorage s, ByteBuf buffer) {
        buffer.writeByte((s.getBitArray().getVersion().getId() << 1) | 1);
        for (int word : s.getBitArray().getWords()) {
            buffer.writeIntLE(word);
        }
        s.getBitArray().writeSizeToNetwork(buffer, s.getPalette().size());
        s.getPalette().forEach((java.util.function.IntConsumer) id -> VarInts.writeInt(buffer, id));
    }

    private static BlockStorage randomStorage(Random r, int distinct) {
        BlockStorage s = new BlockStorage(0);
        int[] ids = new int[distinct];
        for (int i = 0; i < distinct; i++) ids[i] = r.nextInt(30000) + (i == 0 ? 0 : 1);
        for (int i = 0; i < BlockStorage.SIZE; i++) {
            s.setFullBlock(i, ids[r.nextInt(distinct)]);
        }
        return s;
    }

    private static byte[] bytes(ByteBuf b) {
        byte[] out = new byte[b.readableBytes()];
        b.readBytes(out);
        b.release();
        return out;
    }

    @Test
    void storageBytesIdentical() {
        Random r = new Random(1);
        int[] distinctCounts = {1, 2, 3, 5, 9, 17, 40, 100, 300, 1000};
        for (int d : distinctCounts) {
            BlockStorage s = randomStorage(r, d);
            for (int initial : new int[]{0, 1, 16, 256, 100000}) {
                ByteBuf a = Unpooled.buffer(initial == 0 ? 1 : initial);
                ByteBuf b = Unpooled.buffer(initial == 0 ? 1 : initial);
                oldStorageWrite(s, a);
                s.writeToNetwork(b);
                assertArrayEquals(bytes(a), bytes(b), "distinct=" + d + " initial=" + initial);
            }
        }
    }

    @Test
    void sectionBytesIdentical() {
        Random r = new Random(2);
        for (int d : new int[]{1, 4, 20, 200}) {
            BlockStorage[] layers = {randomStorage(r, d), randomStorage(r, Math.max(1, d / 2))};
            GeyserChunkSection section = new GeyserChunkSection(layers, 7);
            ByteBuf a = Unpooled.buffer(64);
            a.writeByte(9);
            a.writeByte(layers.length);
            a.writeByte(7);
            for (BlockStorage l : layers) oldStorageWrite(l, a);
            ByteBuf b = Unpooled.buffer(64);
            section.writeToNetwork(b);
            assertArrayEquals(bytes(a), bytes(b));
        }
    }

    @Test
    void allocatesLessForEqualWork() {
        Random r = new Random(3);
        BlockStorage[] storages = new BlockStorage[12];
        for (int i = 0; i < storages.length; i++) storages[i] = randomStorage(r, new int[]{2, 5, 17, 60}[i % 4]);
        com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long id = Thread.currentThread().getId();
        for (int w = 0; w < 300; w++) { // warm-up
            for (BlockStorage s : storages) {
                ByteBuf a = Unpooled.buffer(); oldStorageWrite(s, a); a.release();
                ByteBuf b = Unpooled.buffer(); s.writeToNetwork(b); b.release();
            }
        }
        long t0 = mx.getThreadAllocatedBytes(id);
        for (int w = 0; w < 200; w++) for (BlockStorage s : storages) { ByteBuf a = Unpooled.buffer(); oldStorageWrite(s, a); a.release(); }
        long oldBytes = mx.getThreadAllocatedBytes(id) - t0;
        t0 = mx.getThreadAllocatedBytes(id);
        for (int w = 0; w < 200; w++) for (BlockStorage s : storages) { ByteBuf b = Unpooled.buffer(); s.writeToNetwork(b); b.release(); }
        long newBytes = mx.getThreadAllocatedBytes(id) - t0;
        System.out.println("PERF chunk storage write alloc old=" + oldBytes + " new=" + newBytes + " ratio=" + (double) oldBytes / newBytes);
        assertTrue(newBytes <= oldBytes, "old=" + oldBytes + " new=" + newBytes);
    }
}
