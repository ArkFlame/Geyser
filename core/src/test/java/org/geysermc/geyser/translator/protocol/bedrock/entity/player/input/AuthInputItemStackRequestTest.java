/*
 * Copyright (c) 2026 GeyserMC. http://geysermc.org
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
 */

package org.geysermc.geyser.translator.protocol.bedrock.entity.player.input;

import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequest;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;
import org.geysermc.geyser.inventory.InventoryHolder;
import org.geysermc.geyser.session.GeyserSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * #3368: an item stack request that arrives inside PlayerAuthInput (recipe book auto-craft on some devices) must
 * go to the inventory that is open, as the standalone ItemStackRequestPacket does, not always the player inventory.
 */
class AuthInputItemStackRequestTest {
    @SuppressWarnings("unchecked")
    private final InventoryHolder<?> playerHolder = mock(InventoryHolder.class);
    @SuppressWarnings("unchecked")
    private final InventoryHolder<?> openHolder = mock(InventoryHolder.class);
    private final GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
    private final ItemStackRequest request = mock(ItemStackRequest.class);

    private void send() {
        when(session.getSpectatedEntity()).thenReturn(null); // deep stubs would otherwise report a spectated entity
        when(session.getPlayerInventoryHolder()).thenReturn((InventoryHolder) playerHolder);
        PlayerAuthInputPacket packet = new PlayerAuthInputPacket();
        packet.getInputData().add(PlayerAuthInputData.PERFORM_ITEM_STACK_REQUEST);
        packet.setItemStackRequest(request);
        new BedrockPlayerAuthInputTranslator().translate(session, packet);
    }

    @Test
    void requestGoesToTheOpenCraftingTable() {
        when(session.getInventoryHolder()).thenReturn((InventoryHolder) openHolder);
        send();
        verify(openHolder).translateRequests(List.of(request));
        verify(playerHolder, never()).translateRequests(any());
    }

    @Test
    void requestGoesToThePlayerInventoryWhenNothingIsOpen() {
        when(session.getInventoryHolder()).thenReturn(null);
        send();
        verify(playerHolder).translateRequests(List.of(request));
        verify(openHolder, never()).translateRequests(any());
    }
}
