/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.translator.inventory;

import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequest;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequestSlotData;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ConsumeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.CraftRecipeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.CraftResultsDeprecatedAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.PlaceAction;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackResponsePacket;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponse;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseStatus;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.GeyserResources;
import org.geysermc.geyser.inventory.Container;
import org.geysermc.geyser.inventory.GeyserItemStack;
import org.geysermc.geyser.inventory.click.Click;
import org.geysermc.geyser.inventory.click.ClickPlan;
import org.geysermc.geyser.registry.java.BuiltInJavaRegistries;
import org.geysermc.geyser.registry.populator.DataComponentRegistryPopulator;
import org.geysermc.geyser.session.GeyserSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * Crafting-table ItemStackRequests, checked as the Java clicks they turn into.
 * The request shapes are the ones in the reports: #5290 (shift-crafting iron ingots) and #5611 (Pocket UI
 * predicting a firework rocket into the grid).
 */
class CraftingRequestTranslationTest {
    private static final ItemDefinition RESULT = new SimpleItemDefinition("minecraft:iron_ingot", 1, false);

    private CraftingInventoryTranslator translator;
    private MockedStatic<org.geysermc.geyser.text.GeyserLocale> locale;
    private final GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
    private final Container inventory = mock(Container.class, RETURNS_DEEP_STUBS);
    private final List<String> clicks = new ArrayList<>();
    private MockedConstruction<ClickPlan> plans;
    private MockedStatic<GeyserImpl> geyser;

    @BeforeAll
    static void bootstrapRegistries() {
        mockContext(() -> {
            DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
        });
    }

    /**
     * InventoryTranslator's static initialiser builds every translator, and the shulker one reads the block-entity
     * registry, whose real load needs a full bootstrap. Mark it loaded and empty, which is all those translators need.
     */
    @BeforeAll
    static void emptyBlockEntityRegistry() throws Exception {
        Object registry = org.geysermc.geyser.registry.Registries.BLOCK_ENTITIES;
        Class<?> deferred = Class.forName("org.geysermc.geyser.registry.DeferredRegistry");
        java.lang.reflect.Method backing = deferred.getDeclaredMethod("backingRegistry");
        backing.setAccessible(true);
        Object inner = backing.invoke(registry);
        inner.getClass().getMethod("set", Object.class).invoke(inner, new java.util.HashMap<>());
        java.lang.reflect.Field loaded = deferred.getDeclaredField("loaded");
        loaded.setAccessible(true);
        loaded.setBoolean(registry, true);
    }

    @BeforeEach
    void recordClicks() {
        // InventoryTranslator's static initialiser logs through GeyserLocale, which is not loaded offline.
        locale = mockStatic(org.geysermc.geyser.text.GeyserLocale.class);
        locale.when(() -> org.geysermc.geyser.text.GeyserLocale.getLocaleStringLog(any())).thenReturn("");
        this.translator = new CraftingInventoryTranslator();
        GeyserImpl impl = mock(GeyserImpl.class, RETURNS_DEEP_STUBS);
        when(impl.config().debugMode()).thenReturn(false);
        geyser = mockStatic(GeyserImpl.class);
        geyser.when(GeyserImpl::getInstance).thenReturn(impl);
        plans = mockConstruction(ClickPlan.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS), (plan, context) -> {
            GeyserItemStack emptyCursor = mock(GeyserItemStack.class);
            when(emptyCursor.isEmpty()).thenReturn(true);
            when(plan.getCursor()).thenReturn(emptyCursor);
            doAnswer(invocation -> clicks.add(invocation.<Click>getArgument(0) + "@" + invocation.<Integer>getArgument(1)))
                .when(plan).add(any(Click.class), anyInt());
        });
    }

    @AfterEach
    void close() {
        if (plans != null) plans.close();
        if (geyser != null) geyser.close();
        if (locale != null) locale.close();
    }

    private static ItemStackRequestSlotData slot(ContainerSlotType type, int slot) {
        return new ItemStackRequestSlotData(type, slot, -1, new FullContainerName(type, null));
    }

    private ItemStackResponse translate(ItemStackRequestAction... actions) {
        return translator.translateCraftingRequest(session, inventory, new ItemStackRequest(1, actions, new String[0]));
    }

    private static ItemStackRequestAction result(int count) {
        return new CraftResultsDeprecatedAction(new ItemData[]{ItemData.builder().definition(RESULT).count(count).build()}, 1);
    }

    /** #5290: Bedrock 1.20.51 shift-click on a recipe that makes 9 ingots, requested twice. */
    @Test
    void shiftCraftingTwiceIsOneShiftClickOnTheOutput() {
        ItemStackResponse response = translate(
            new CraftRecipeAction(621, 2),
            result(9),
            new ConsumeAction(2, slot(ContainerSlotType.CRAFTING_INPUT, 36)),
            new PlaceAction(8, slot(ContainerSlotType.CREATED_OUTPUT, 50), slot(ContainerSlotType.HOTBAR_AND_INVENTORY, 14)),
            new PlaceAction(10, slot(ContainerSlotType.CREATED_OUTPUT, 50), slot(ContainerSlotType.HOTBAR_AND_INVENTORY, 15)));

        assertEquals(ItemStackResponseStatus.OK, response.getResult());
        assertEquals(List.of("LEFT_SHIFT@0"), clicks);
    }

    /** A single craft is unchanged: take the output, put it where the client said. */
    @Test
    void aSingleCraftIsStillTakenAndPlaced() {
        ItemStackResponse response = translate(
            new CraftRecipeAction(621, 1),
            result(9),
            new ConsumeAction(1, slot(ContainerSlotType.CRAFTING_INPUT, 36)),
            new PlaceAction(9, slot(ContainerSlotType.CREATED_OUTPUT, 50), slot(ContainerSlotType.HOTBAR_AND_INVENTORY, 14)));

        assertEquals(ItemStackResponseStatus.OK, response.getResult());
        assertEquals(List.of("LEFT@0", "LEFT@" + translator.bedrockSlotToJava(slot(ContainerSlotType.HOTBAR_AND_INVENTORY, 14))), clicks);
    }

    /** #5611: the Pocket UI predicts the rocket into the grid cell it just consumed. */
    @Test
    void aCraftedResultIsNeverPlacedBackIntoTheCraftingGrid() {
        ItemStackResponse response = translate(
            new CraftRecipeAction(-5, 1),
            result(3),
            new ConsumeAction(1, slot(ContainerSlotType.CRAFTING_INPUT, 36)),
            new PlaceAction(3, slot(ContainerSlotType.CREATED_OUTPUT, 50), slot(ContainerSlotType.CRAFTING_INPUT, 36)));

        assertEquals(ItemStackResponseStatus.OK, response.getResult());
        // Only the output is picked up (onto the cursor); the gunpowder in grid slot 5 is not overwritten.
        assertEquals(List.of("LEFT@0"), clicks);
    }
}
