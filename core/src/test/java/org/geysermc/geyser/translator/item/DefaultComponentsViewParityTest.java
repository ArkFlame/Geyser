package org.geysermc.geyser.translator.item;

import org.cloudburstmc.nbt.NbtMap;
import org.geysermc.geyser.GeyserResources;
import org.geysermc.geyser.item.Items;
import org.geysermc.geyser.item.TooltipOptions;
import org.geysermc.geyser.item.components.resolvable.ResolvableComponentGetter;
import org.geysermc.geyser.item.type.Item;
import org.geysermc.geyser.registry.java.BuiltInJavaRegistries;
import org.geysermc.geyser.registry.populator.DataComponentRegistryPopulator;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * The cached default components view must not change the Bedrock NBT (including when GeyserHash is added)
 * and must make the common no-merge case cheaper than the previous clone + full-equality algorithm.
 */
class DefaultComponentsViewParityTest {

    private static Object field(Item item, String name) throws Exception {
        Field f = Item.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(item);
    }

    private static void bootstrap() {
        DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
        BuiltInJavaRegistries.bootstrap();
        BuiltInJavaRegistries.freeze();
    }

    /** Previous gatherComponents for the no-merge case: a fresh copy wrapped in an unmodifiable view. */
    private static DataComponents oldGather(Item item) throws Exception {
        DataComponents base = ((DataComponents) field(item, "baseComponents")).clone();
        return new DataComponents(Collections.unmodifiableMap(base.getDataComponents()));
    }

    private static NbtMap translate(Item item, GeyserSession session, DataComponents components) {
        BedrockItemBuilder builder = new BedrockItemBuilder();
        item.translateComponentsToBedrock(session, components, TooltipOptions.fromComponents(components), builder);
        return builder.build();
    }

    @Test
    void sameNbtAndSharedViewInstance() {
        mockContext(() -> {
            bootstrap();
            GeyserSession session = mock(GeyserSession.class, withSettings().stubOnly());
            ResolvableComponentGetter getter = mock(ResolvableComponentGetter.class, withSettings().stubOnly());
            int shared = 0;
            try {
                for (Item item : new Item[]{Items.STONE, Items.DIAMOND_SWORD, Items.FILLED_MAP, Items.PLAYER_HEAD, Items.DIAMOND}) {
                    DataComponents view = item.gatherComponents(getter, null);
                    boolean fast = ((List<?>) field(item, "resolvableComponents")).isEmpty();
                    if (fast) {
                        assertSame(view, item.gatherComponents(getter, null), item.toString());
                        assertSame(view, item.gatherComponents(getter, new DataComponents(new HashMap<>())), item.toString());
                        shared++;
                    }
                    // Default components: no GeyserHash either way
                    assertEquals(translate(item, session, oldGather(item)), translate(item, session, view), item.toString());
                    // Changed components: GeyserHash is still added
                    DataComponents changed = new DataComponents(new HashMap<>());
                    changed.put(DataComponentTypes.MAX_STACK_SIZE, 7);
                    DataComponents merged = item.gatherComponents(getter, changed);
                    NbtMap changedNbt = translate(item, session, merged);
                    assertTrue(Objects.requireNonNull(changedNbt).containsKey("GeyserHash"), item.toString());
                    assertEquals(merged.hashCode(), changedNbt.getInt("GeyserHash"));
                }
            } catch (Exception e) {
                throw new AssertionError(e);
            }
            assertTrue(shared > 0, "no tested item took the cached-view path");
        });
    }

    @Test
    void cheaperThanCloneAndFullEquals() {
        mockContext(() -> {
            bootstrap();
            GeyserSession session = mock(GeyserSession.class, withSettings().stubOnly());
            ResolvableComponentGetter getter = mock(ResolvableComponentGetter.class, withSettings().stubOnly());
            Item item = Items.DIAMOND_SWORD;
            com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) java.lang.management.ManagementFactory.getThreadMXBean();
            long id = Thread.currentThread().getId();
            try {
                assertTrue(((List<?>) field(item, "resolvableComponents")).isEmpty());
                for (int i = 0; i < 30000; i++) {
                    DataComponents o = oldGather(item);
                    item.translateComponentsToBedrock(session, o, TooltipOptions.fromComponents(o), new BedrockItemBuilder());
                    DataComponents n = item.gatherComponents(getter, null);
                    item.translateComponentsToBedrock(session, n, TooltipOptions.fromComponents(n), new BedrockItemBuilder());
                }
                int n = 20000;
                long bestOldNs = Long.MAX_VALUE, bestNewNs = Long.MAX_VALUE, oldBytes = 0, newBytes = 0;
                for (int round = 0; round < 5; round++) {
                    long t0 = mx.getThreadAllocatedBytes(id), c0 = System.nanoTime();
                    for (int i = 0; i < n; i++) {
                        DataComponents o = oldGather(item);
                        item.translateComponentsToBedrock(session, o, TooltipOptions.fromComponents(o), new BedrockItemBuilder());
                    }
                    bestOldNs = Math.min(bestOldNs, System.nanoTime() - c0);
                    oldBytes = (mx.getThreadAllocatedBytes(id) - t0) / n;
                    t0 = mx.getThreadAllocatedBytes(id);
                    c0 = System.nanoTime();
                    for (int i = 0; i < n; i++) {
                        DataComponents v = item.gatherComponents(getter, null);
                        item.translateComponentsToBedrock(session, v, TooltipOptions.fromComponents(v), new BedrockItemBuilder());
                    }
                    bestNewNs = Math.min(bestNewNs, System.nanoTime() - c0);
                    newBytes = (mx.getThreadAllocatedBytes(id) - t0) / n;
                }
                System.out.println("PERF default-view diamond_sword bytes/call old=" + oldBytes + " new=" + newBytes
                    + " | ns/call old=" + bestOldNs / n + " new=" + bestNewNs / n
                    + " cpu-ratio=" + (double) bestOldNs / bestNewNs);
                assertTrue(newBytes < oldBytes, "old=" + oldBytes + " new=" + newBytes);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
    }
}
