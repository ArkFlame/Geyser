package org.geysermc.geyser.translator.item;

import org.geysermc.geyser.GeyserResources;
import org.geysermc.geyser.inventory.GeyserItemStack;
import org.geysermc.geyser.item.Items;
import org.geysermc.geyser.registry.java.BuiltInJavaRegistries;
import org.geysermc.geyser.registry.populator.DataComponentRegistryPopulator;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/** getBedrockItemDefinition merges the stack's components once, not once per consumer. */
class BedrockItemDefinitionComponentsOnceTest {

    @Test
    void mergesComponentsOnlyOnce() {
        mockContext(() -> {
            DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            DataComponents patch = new DataComponents(new HashMap<>());
            patch.put(DataComponentTypes.MAX_STACK_SIZE, 7);
            GeyserItemStack stack = spy(GeyserItemStack.of(session, Items.DIAMOND_SWORD.javaId(), 1, patch));

            var first = ItemTranslator.getBedrockItemDefinition(session, stack);
            verify(stack, times(1)).getAllComponents();
            // Deterministic: same stubs, same definition
            assertSame(first, ItemTranslator.getBedrockItemDefinition(session, stack));
        });
    }
}
