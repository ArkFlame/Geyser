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
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
            try { Class.forName(ItemTranslator.class.getName()); } catch (ClassNotFoundException e) { throw new AssertionError(e); }
        });
    }

    @Test
    void translatesPlainItemOffline() {
        mockContext(() -> {
            DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            ItemMapping mapping = mock(ItemMapping.class, RETURNS_DEEP_STUBS);
            ItemData.Builder b = ItemTranslator.translateToBedrock(session, Items.DIAMOND, mapping, 3, null);
            assertNotNull(b);
        });
    }
}
