package org.geysermc.geyser.translator.item;

import org.geysermc.geyser.registry.populator.DataComponentRegistryPopulator;
import org.geysermc.geyser.GeyserResources;
import org.geysermc.geyser.registry.java.BuiltInJavaRegistries;
import org.junit.jupiter.api.Test;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;

class ItemTranslatorInitProbeTest {
    @Test
    void itemTranslatorInitialisesAfterBootstrap() {
        mockContext(() -> {
            DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
            try { Class.forName(ItemTranslator.class.getName()); } catch (ClassNotFoundException e) { throw new AssertionError(e); }
        });
    }
}
