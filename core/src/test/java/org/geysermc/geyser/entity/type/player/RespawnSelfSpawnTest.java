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

package org.geysermc.geyser.entity.type.player;

import org.cloudburstmc.math.vector.Vector3f;
import org.geysermc.geyser.GeyserResources;
import org.geysermc.geyser.entity.VanillaEntities;
import org.geysermc.geyser.entity.spawn.EntitySpawnContext;
import org.geysermc.geyser.registry.java.BuiltInJavaRegistries;
import org.geysermc.geyser.registry.populator.DataComponentRegistryPopulator;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.session.cache.EntityCache;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RespawnSelfSpawnTest {
    // The Floodgate-style (version 9) UUID quoted verbatim in issue #3048.
    private static final UUID SELF_UUID = UUID.fromString("00000000-0000-0000-0009-01f2565baad8");
    private static final UUID OTHER_UUID = UUID.fromString("00000000-0000-0000-0009-01f2565baad9");

    @BeforeAll
    static void bootstrapRegistries() {
        mockContext(() -> {
            DataComponentRegistryPopulator.load(GeyserResources.DEFAULT);
            BuiltInJavaRegistries.bootstrap();
            BuiltInJavaRegistries.freeze();
        });
    }

    /** Mirrors JavaAddEntityTranslator:80-82 -> 100 when the server re-adds the local player on respawn. */
    @Test
    void selfPlayerSpawnIsNotDroppedWhenNoPlayerListEntryWasSeen() {
        mockContext(() -> {
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            when(session.getPlayerEntity().uuid()).thenReturn(SELF_UUID);

            // The session player lives on the session, never in EntityCache.playerEntities.
            EntityCache cache = mock(EntityCache.class);
            when(session.getEntityCache()).thenReturn(cache);
            when(cache.getPlayerEntity(SELF_UUID)).thenReturn(null);

            EntitySpawnContext context = new EntitySpawnContext(session, VanillaEntities.PLAYER, 42, SELF_UUID);
            context.position(Vector3f.from(0, 64, 0));
            PlayerEntity self = new PlayerEntity(context, "BedrockPlayer", null);

            self.sendPlayer();

            // FAILS on current code: PlayerEntity.sendPlayer():147 returns early.
            verify(cache).spawnEntity(self);
        });
    }

    /** Negative case: other players must still require a prior PlayerListEntry (failed_list guard). */
    @Test
    void otherPlayerSpawnStillRequiresPlayerListEntry() {
        mockContext(() -> {
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            when(session.getPlayerEntity().uuid()).thenReturn(SELF_UUID);

            EntityCache cache = mock(EntityCache.class);
            when(session.getEntityCache()).thenReturn(cache);
            when(cache.getPlayerEntity(OTHER_UUID)).thenReturn(null);

            EntitySpawnContext context = new EntitySpawnContext(session, VanillaEntities.PLAYER, 43, OTHER_UUID);
            context.position(Vector3f.from(0, 64, 0));
            PlayerEntity other = new PlayerEntity(context, "OtherPlayer", null);

            other.sendPlayer();

            verify(cache, never()).spawnEntity(any());
        });
    }
}
