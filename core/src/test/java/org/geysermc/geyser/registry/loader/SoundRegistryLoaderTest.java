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
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.registry.loader;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlaySoundPacket;
import org.geysermc.geyser.GeyserBootstrap;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.registry.Registries;
import org.geysermc.geyser.registry.type.SoundMapping;
import org.geysermc.geyser.scoreboard.network.util.GeyserMockContext;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.util.SoundUtils;
import org.geysermc.mcprotocollib.protocol.data.game.level.sound.BuiltinSound;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class SoundRegistryLoaderTest {
    private static final String SWEEP = "entity.player.attack.sweep";
    private static final String INTEGRATED_SWEEP = "geyseropt.entity.player.attack.sweep";

    @Test
    public void appliesForkOverridesOverSubmoduleMappings() {
        GeyserMockContext.mockContext(context -> {
            mockResources(context);

            Map<String, SoundMapping> mappings = new SoundRegistryLoader().load("mappings/sounds.json");
            SoundMapping sweep = mappings.get(SWEEP);

            assertEquals(INTEGRATED_SWEEP, sweep.integratedPlaySound());
            assertNull(sweep.playsound());
            assertEquals("ATTACK", sweep.bedrock());
        });
    }

    @Test
    public void selectsIntegratedPlaysoundOnlyWithIntegratedPack() {
        GeyserMockContext.mockContext(context -> {
            mockResources(context);
            Registries.SOUNDS.load();

            GeyserSession session = context.mock(GeyserSession.class);
            List<BedrockPacket> packets = new ArrayList<>();
            doAnswer(invocation -> {
                packets.add(invocation.getArgument(0));
                return null;
            }).when(session).sendUpstreamPacket(any());

            when(session.integratedPackActive()).thenReturn(true);
            SoundUtils.playSound(session, BuiltinSound.ENTITY_PLAYER_ATTACK_SWEEP, Vector3f.ZERO, 1.0f, 1.0f);
            assertEquals(INTEGRATED_SWEEP, assertInstanceOf(PlaySoundPacket.class, packets.getFirst()).getSound());

            packets.clear();
            when(session.integratedPackActive()).thenReturn(false);
            SoundUtils.playSound(session, BuiltinSound.ENTITY_PLAYER_ATTACK_SWEEP, Vector3f.ZERO, 1.0f, 1.0f);
            assertEquals(SoundEvent.ATTACK, assertInstanceOf(LevelSoundEventPacket.class, packets.getFirst()).getSound());
        });
    }

    private static void mockResources(GeyserMockContext context) {
        GeyserBootstrap bootstrap = context.mock(GeyserBootstrap.class);
        when(bootstrap.getResourceOrThrow(anyString())).thenAnswer(invocation -> resource(invocation.getArgument(0)));
        when(bootstrap.getResourceOrNull(anyString())).thenAnswer(invocation -> resource(invocation.getArgument(0)));
        when(context.mockOrSpy(GeyserImpl.class).getBootstrap()).thenReturn(bootstrap);
    }

    private static InputStream resource(String name) {
        return SoundRegistryLoaderTest.class.getClassLoader().getResourceAsStream(name);
    }
}
