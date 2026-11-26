package org.geysermc.geyser.translator.item;

import org.geysermc.geyser.GeyserResources;
import org.geysermc.geyser.item.Items;
import org.geysermc.geyser.item.components.resolvable.ResolvableComponentGetter;
import org.geysermc.geyser.item.type.Item;
import org.geysermc.geyser.registry.java.BuiltInJavaRegistries;
import org.geysermc.geyser.registry.populator.DataComponentRegistryPopulator;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentType;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/** Item.hasComponent must answer exactly like gatherComponents(...).contains(...), without building the merged map. */
class HasComponentParityTest {

    private static final List<DataComponentType<?>> TYPES = List.of(
        DataComponentTypes.MAX_STACK_SIZE, DataComponentTypes.DAMAGE, DataComponentTypes.UNBREAKABLE,
        DataComponentTypes.ENCHANTMENTS, DataComponentTypes.CUSTOM_NAME, DataComponentTypes.LORE,
        DataComponentTypes.RARITY, DataComponentTypes.DYED_COLOR, DataComponentTypes.POTION_CONTENTS,
        DataComponentTypes.MAX_DAMAGE, DataComponentTypes.REPAIR_COST);

    private static void bootstrap() {
        DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
        BuiltInJavaRegistries.bootstrap();
        BuiltInJavaRegistries.freeze();
    }

    @Test
    void sameAnswerForAllItemsPatchesAndTypes() {
        mockContext(() -> {
            bootstrap();
            ResolvableComponentGetter noResolved = mock(ResolvableComponentGetter.class, withSettings().stubOnly());
            DataComponents resolvedSet = new DataComponents(new HashMap<>());
            resolvedSet.put(DataComponentTypes.POTION_CONTENTS, null);
            resolvedSet.getDataComponents().put(DataComponentTypes.DYED_COLOR, null);
            ResolvableComponentGetter withResolved = mock(ResolvableComponentGetter.class, withSettings().stubOnly());
            when(withResolved.getResolvedComponents(Items.DIAMOND)).thenReturn(resolvedSet);

            DataComponents emptyPatch = new DataComponents(new HashMap<>());
            DataComponents valuePatch = new DataComponents(new HashMap<>());
            valuePatch.put(DataComponentTypes.MAX_STACK_SIZE, 7);
            DataComponents removalPatch = new DataComponents(new HashMap<>());
            removalPatch.getDataComponents().put(DataComponentTypes.UNBREAKABLE, null); // key present, value null
            removalPatch.getDataComponents().put(DataComponentTypes.REPAIR_COST, null);
            DataComponents[] patches = {null, emptyPatch, valuePatch, removalPatch};

            int checked = 0, trueAnswers = 0;
            for (Item item : new Item[]{Items.STONE, Items.DIAMOND_SWORD, Items.DIAMOND, Items.PLAYER_HEAD, Items.FILLED_MAP}) {
                for (ResolvableComponentGetter getter : new ResolvableComponentGetter[]{noResolved, withResolved}) {
                    for (DataComponents patch : patches) {
                        for (DataComponentType<?> type : TYPES) {
                            boolean expected = item.gatherComponents(getter, patch).contains(type);
                            boolean actual = item.hasComponent(getter, patch, type);
                            assertEquals(expected, actual, item + " patch=" + patch + " type=" + type);
                            checked++;
                            if (expected) trueAnswers++;
                        }
                    }
                }
            }
            assertTrue(trueAnswers > 10 && trueAnswers < checked, "test must cover both true and false answers: " + trueAnswers + "/" + checked);
        });
    }

    @Test
    void cheaperWhenStackCarriesAPatch() {
        mockContext(() -> {
            bootstrap();
            ResolvableComponentGetter getter = mock(ResolvableComponentGetter.class, withSettings().stubOnly());
            Item item = Items.DIAMOND_SWORD;
            DataComponents patch = new DataComponents(new HashMap<>());
            patch.put(DataComponentTypes.MAX_STACK_SIZE, 7);
            DataComponentType<?> type = DataComponentTypes.UNBREAKABLE;
            com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) java.lang.management.ManagementFactory.getThreadMXBean();
            long id = Thread.currentThread().getId();
            int n = 30000;
            for (int i = 0; i < 60000; i++) {
                item.gatherComponents(getter, patch).contains(type);
                item.hasComponent(getter, patch, type);
            }
            long bestOldNs = Long.MAX_VALUE, bestNewNs = Long.MAX_VALUE, oldBytes = 0, newBytes = 0;
            int sink = 0;
            for (int round = 0; round < 5; round++) {
                long t0 = mx.getThreadAllocatedBytes(id), c0 = System.nanoTime();
                for (int i = 0; i < n; i++) if (item.gatherComponents(getter, patch).contains(type)) sink++;
                bestOldNs = Math.min(bestOldNs, System.nanoTime() - c0);
                oldBytes = (mx.getThreadAllocatedBytes(id) - t0) / n;
                t0 = mx.getThreadAllocatedBytes(id);
                c0 = System.nanoTime();
                for (int i = 0; i < n; i++) if (item.hasComponent(getter, patch, type)) sink++;
                bestNewNs = Math.min(bestNewNs, System.nanoTime() - c0);
                newBytes = (mx.getThreadAllocatedBytes(id) - t0) / n;
            }
            System.out.println("PERF hasComponent diamond_sword+patch bytes/call old=" + oldBytes + " new=" + newBytes
                + " | ns/call old=" + bestOldNs / n + " new=" + bestNewNs / n
                + " cpu-ratio=" + (double) bestOldNs / bestNewNs + " sink=" + sink);
            assertTrue(newBytes < oldBytes, "old=" + oldBytes + " new=" + newBytes);
        });
    }
}
