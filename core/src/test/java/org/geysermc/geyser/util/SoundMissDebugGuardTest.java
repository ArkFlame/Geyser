package org.geysermc.geyser.util;

import org.cloudburstmc.math.vector.Vector3f;
import org.geysermc.geyser.GeyserBootstrap;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.GeyserLogger;
import org.geysermc.geyser.registry.Registries;
import org.geysermc.geyser.registry.type.SoundMapping;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.level.sound.BuiltinSound;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.Map;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unmapped or unplayable sounds still behave the same, but only build their debug text when debug logging is on. */
class SoundMissDebugGuardTest {

    private static void loadSounds() {
        GeyserBootstrap bootstrap = Mockito.mock(GeyserBootstrap.class);
        when(bootstrap.getResourceOrThrow(anyString())).thenAnswer(inv -> {
            InputStream s = SoundMissDebugGuardTest.class.getClassLoader().getResourceAsStream(inv.<String>getArgument(0));
            if (s == null) throw new IllegalStateException("missing " + inv.getArgument(0));
            return s;
        });
        when(bootstrap.getResourceOrNull(anyString())).thenAnswer(inv -> SoundMissDebugGuardTest.class.getClassLoader().getResourceAsStream(inv.<String>getArgument(0)));
        when(GeyserImpl.getInstance().getBootstrap()).thenReturn(bootstrap);
        Registries.SOUNDS.load();
    }

    private static BuiltinSound unmappedSound() {
        for (BuiltinSound sound : BuiltinSound.values()) {
            if (Registries.SOUNDS.get(sound.getName()) == null) {
                return sound;
            }
        }
        return null;
    }

    private static String unplayableMappedId() {
        for (Map.Entry<String, SoundMapping> e : Registries.SOUNDS.get().entrySet()) {
            SoundMapping m = e.getValue();
            if (m.playsound() == null && !m.levelEvent()
                && SoundUtils.toSoundEvent(m.bedrock()) == null && SoundUtils.toSoundEvent(e.getKey()) == null) {
                return e.getKey();
            }
        }
        return null;
    }

    private static GeyserSession session(GeyserLogger logger) {
        GeyserSession session = mock(GeyserSession.class, Mockito.RETURNS_DEEP_STUBS);
        when(session.getGeyser().getLogger()).thenReturn(logger);
        return session;
    }

    @Test
    void unmappedSoundPlaysAsCustomWithoutDebugText() {
        mockContext(() -> {
            loadSounds();
            org.geysermc.mcprotocollib.protocol.data.game.level.sound.Sound sound = new org.geysermc.mcprotocollib.protocol.data.game.level.sound.CustomSound("minecraft:geyserfast.not_a_real_sound", false, 0f);
            
            GeyserLogger off = mock(GeyserLogger.class);
            when(off.isDebug()).thenReturn(false);
            GeyserSession s1 = session(off);
            SoundUtils.playSound(s1, sound, Vector3f.ZERO, 1f, 1f);
            verify(s1, times(1)).sendUpstreamPacket(Mockito.any()); // still plays as a custom sound
            verify(off, never()).debug(anyString());

            GeyserLogger on = mock(GeyserLogger.class);
            when(on.isDebug()).thenReturn(true);
            GeyserSession s2 = session(on);
            SoundUtils.playSound(s2, sound, Vector3f.ZERO, 1f, 1f);
            verify(s2, times(1)).sendUpstreamPacket(Mockito.any());
            verify(on).debug("[Builtin] Sound mapping for geyserfast.not_a_real_sound not found; assuming custom.");
        });
    }

    @Test
    void unplayableMappedSoundSendsNothingAndSkipsDebugText() {
        mockContext(() -> {
            loadSounds();
            String id = unplayableMappedId();
            assertNotNull(id, "need a mapped sound that is not a playable level sound");
            SoundMapping m = Registries.SOUNDS.get(id);
            org.geysermc.mcprotocollib.protocol.data.game.level.sound.Sound custom = new org.geysermc.mcprotocollib.protocol.data.game.level.sound.CustomSound(id, false, 0f);
            GeyserLogger off = mock(GeyserLogger.class);
            when(off.isDebug()).thenReturn(false);
            GeyserSession s1 = session(off);
            SoundUtils.playSound(s1, custom, Vector3f.ZERO, 1f, 1f);
            verify(s1, never()).sendUpstreamPacket(Mockito.any());
            verify(off, never()).debug(anyString());

            GeyserLogger on = mock(GeyserLogger.class);
            when(on.isDebug()).thenReturn(true);
            GeyserSession s2 = session(on);
            SoundUtils.playSound(s2, custom, Vector3f.ZERO, 1f, 1f);
            verify(s2, never()).sendUpstreamPacket(Mockito.any());
            verify(on).debug("[Builtin] Sound for original '" + id + "' to mappings '" + m.bedrock()
                + "' was not a playable level sound, or has yet to be mapped to an enum in SoundEvent.");
        });
    }
}
