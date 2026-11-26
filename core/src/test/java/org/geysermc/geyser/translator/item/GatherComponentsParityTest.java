package org.geysermc.geyser.translator.item;

import org.geysermc.geyser.GeyserResources;
import org.geysermc.geyser.item.Items;
import org.geysermc.geyser.item.components.resolvable.ResolvableComponentGetter;
import org.geysermc.geyser.item.type.Item;
import org.geysermc.geyser.registry.java.BuiltInJavaRegistries;
import org.geysermc.geyser.registry.populator.DataComponentRegistryPopulator;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/** gatherComponents must return the same components as the clone-based algorithm, and allocate less when nothing merges. */
class GatherComponentsParityTest {

    private static Object field(Item item, String name) throws Exception {
        Field f = Item.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(item);
    }

    /** The previous implementation, kept verbatim for comparison. */
    private static DataComponents oldGather(Item item, ResolvableComponentGetter getter, DataComponents others) throws Exception {
        DataComponents base = ((DataComponents) field(item, "baseComponents")).clone();
        List<?> resolvable = (List<?>) field(item, "resolvableComponents");
        if (!resolvable.isEmpty()) {
            DataComponents resolved = getter.getResolvedComponents(item);
            if (resolved != null) {
                base.getDataComponents().putAll(resolved.getDataComponents());
            }
        }
        if (others != null) {
            base.getDataComponents().putAll(others.getDataComponents());
        }
        return new DataComponents(Collections.unmodifiableMap(base.getDataComponents()));
    }

    private static void bootstrap() {
        DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
        BuiltInJavaRegistries.bootstrap();
        BuiltInJavaRegistries.freeze();
    }

    @Test
    void sameComponentsForEveryOthersVariant() {
        mockContext(() -> {
            bootstrap();
            ResolvableComponentGetter getter = mock(ResolvableComponentGetter.class);
            Item[] items = {Items.DIAMOND, Items.PLAYER_HEAD, Items.FILLED_MAP, Items.DIAMOND_SWORD, Items.STONE};
            DataComponents nonEmpty = new DataComponents(new HashMap<>());
            nonEmpty.put(DataComponentTypes.MAX_STACK_SIZE, 16);
            DataComponents[] othersVariants = {null, new DataComponents(new HashMap<>()), nonEmpty};
            int fastPathHits = 0;
            try {
                for (Item item : items) {
                    for (DataComponents others : othersVariants) {
                        DataComponents expected = oldGather(item, getter, others);
                        DataComponents actual = item.gatherComponents(getter, others);
                        assertEquals(expected, actual, item + " others=" + others);
                        assertEquals(expected.getDataComponents(), actual.getDataComponents());
                        assertThrows(UnsupportedOperationException.class,
                            () -> actual.getDataComponents().put(DataComponentTypes.MAX_STACK_SIZE, null));
                        if (((List<?>) field(item, "resolvableComponents")).isEmpty() && (others == null || others.getDataComponents().isEmpty())) {
                            fastPathHits++;
                        }
                    }
                }
            } catch (Exception e) {
                throw new AssertionError(e);
            }
            assertTrue(fastPathHits > 0, "no tested item exercised the fast path");
        });
    }

    @Test
    void allocatesLessWhenNothingToMerge() {
        mockContext(() -> {
            bootstrap();
            ResolvableComponentGetter getter = mock(ResolvableComponentGetter.class);
            Item item = null;
            try {
                for (Item candidate : new Item[]{Items.DIAMOND, Items.STONE, Items.DIAMOND_SWORD, Items.PLAYER_HEAD, Items.FILLED_MAP}) {
                    boolean empty = ((List<?>) field(candidate, "resolvableComponents")).isEmpty();
                    System.out.println("PERF resolvable-empty " + candidate + "=" + empty);
                    if (empty && item == null) item = candidate;
                }
            } catch (Exception e) {
                throw new AssertionError(e);
            }
            assertTrue(item != null, "no tested item has empty resolvable components");
            final Item measured = item;
            com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) java.lang.management.ManagementFactory.getThreadMXBean();
            long id = Thread.currentThread().getId();
            try {
                for (int i = 0; i < 3000; i++) { oldGather(measured, getter, null); measured.gatherComponents(getter, null); }
                int n = 10000;
                long t0 = mx.getThreadAllocatedBytes(id);
                for (int i = 0; i < n; i++) oldGather(measured, getter, null);
                long oldBytes = (mx.getThreadAllocatedBytes(id) - t0) / n;
                t0 = mx.getThreadAllocatedBytes(id);
                for (int i = 0; i < n; i++) measured.gatherComponents(getter, null);
                long newBytes = (mx.getThreadAllocatedBytes(id) - t0) / n;
                System.out.println("PERF gatherComponents " + measured + " bytes/call old=" + oldBytes + " new=" + newBytes
                    + " ratio=" + (double) oldBytes / Math.max(1, newBytes));
                assertTrue(newBytes < oldBytes, "old=" + oldBytes + " new=" + newBytes);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
    }
}
