package org.geysermc.geyser.translator.item;

import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.geysermc.geyser.GeyserResources;
import org.geysermc.geyser.item.Items;
import org.geysermc.geyser.registry.java.BuiltInJavaRegistries;
import org.geysermc.geyser.registry.populator.DataComponentRegistryPopulator;
import org.geysermc.geyser.registry.type.ItemMapping;
import org.geysermc.geyser.session.GeyserSession;
import org.junit.jupiter.api.Test;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

class ItemTranslatorOfflineHarnessTest {
    @Test
    void itemTranslatorInitialisesAfterBootstrap() {
        mockContext(() -> {
            DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
            ItemTestSupport.repairItems();
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
            try { Class.forName(ItemTranslator.class.getName()); } catch (ClassNotFoundException e) { throw new AssertionError(e); }
        });
    }

    @Test
    void translatesPlainItemOffline() {
        mockContext(() -> {
            DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
            ItemTestSupport.repairItems();
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            ItemMapping mapping = mock(ItemMapping.class, RETURNS_DEEP_STUBS);
            ItemData.Builder b = ItemTranslator.translateToBedrock(session, Items.DIAMOND, mapping, 3, null);
            assertNotNull(b);
        });
    }

    @Test
    void measuresAllocationPerTranslation() {
        mockContext(() -> {
            DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
            ItemTestSupport.repairItems();
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
            GeyserSession session = mock(GeyserSession.class, org.mockito.Mockito.withSettings().stubOnly().defaultAnswer(RETURNS_DEEP_STUBS));
            ItemMapping mapping = mock(ItemMapping.class, org.mockito.Mockito.withSettings().stubOnly().defaultAnswer(RETURNS_DEEP_STUBS));
            com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) java.lang.management.ManagementFactory.getThreadMXBean();
            long id = Thread.currentThread().getId();
            for (int i = 0; i < 2000; i++) ItemTranslator.translateToBedrock(session, Items.DIAMOND, mapping, 3, null);
            int n = 5000;
            long t0 = mx.getThreadAllocatedBytes(id);
            for (int i = 0; i < n; i++) ItemTranslator.translateToBedrock(session, Items.DIAMOND, mapping, 3, null);
            long perCall = (mx.getThreadAllocatedBytes(id) - t0) / n;
            System.out.println("PERF translateToBedrock plain item stubOnly bytes/call=" + perCall);
        });
    }

    private static long bytesPerCall(Runnable r) {
        com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) java.lang.management.ManagementFactory.getThreadMXBean();
        long id = Thread.currentThread().getId();
        for (int i = 0; i < 2000; i++) r.run();
        int n = 5000;
        long t0 = mx.getThreadAllocatedBytes(id);
        for (int i = 0; i < n; i++) r.run();
        return (mx.getThreadAllocatedBytes(id) - t0) / n;
    }

    @Test
    void attributesAllocationPerPiece() {
        mockContext(() -> {
            DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
            ItemTestSupport.repairItems();
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
            GeyserSession session = mock(GeyserSession.class, org.mockito.Mockito.withSettings().stubOnly().defaultAnswer(RETURNS_DEEP_STUBS));
            ItemMapping mapping = mock(ItemMapping.class, org.mockito.Mockito.withSettings().stubOnly().defaultAnswer(RETURNS_DEEP_STUBS));
            var items = Items.DIAMOND;
            var cache = session.getComponentCache();
            var comps = items.gatherComponents(cache, null);
            System.out.println("PERF piece empty-loop=" + bytesPerCall(() -> { }));
            System.out.println("PERF piece session.getComponentCache=" + bytesPerCall(() -> session.getComponentCache()));
            System.out.println("PERF piece gatherComponents=" + bytesPerCall(() -> items.gatherComponents(cache, null)));
            System.out.println("PERF piece TooltipOptions.fromComponents=" + bytesPerCall(() -> org.geysermc.geyser.item.TooltipOptions.fromComponents(comps)));
            System.out.println("PERF piece BedrockItemBuilder.new+build=" + bytesPerCall(() -> new org.geysermc.geyser.translator.item.BedrockItemBuilder().build()));
            System.out.println("PERF piece item.translateToBedrock=" + bytesPerCall(() -> items.translateToBedrock(session, 3, comps, mapping, session.getItemMappings())));
            System.out.println("PERF piece getCustomName=" + bytesPerCall(() -> ItemTranslator.getCustomName(session, null, mapping, 'f', false, false)));
            System.out.println("PERF piece whole=" + bytesPerCall(() -> ItemTranslator.translateToBedrock(session, items, mapping, 3, null)));
        });
    }
}
