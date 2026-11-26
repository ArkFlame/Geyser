package org.geysermc.geyser.translator.protocol.java.level;

import org.geysermc.geyser.GeyserBootstrap;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.GeyserLogger;
import org.geysermc.geyser.registry.Registries;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.level.particle.Particle;
import org.geysermc.mcprotocollib.protocol.data.game.level.particle.ParticleType;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundLevelParticlesPacket;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.lang.management.ManagementFactory;

import static org.geysermc.geyser.scoreboard.network.util.GeyserMockContext.mockContext;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/** An unmapped particle sends nothing and must not build the packet's toString unless debug logging is on. */
class ParticleMissDebugGuardTest {

    private static void loadParticles() {
        GeyserBootstrap bootstrap = Mockito.mock(GeyserBootstrap.class);
        when(bootstrap.getResourceOrThrow(anyString())).thenAnswer(inv -> {
            InputStream s = ParticleMissDebugGuardTest.class.getClassLoader().getResourceAsStream(inv.<String>getArgument(0));
            if (s == null) throw new IllegalStateException("missing " + inv.getArgument(0));
            return s;
        });
        when(GeyserImpl.getInstance().getBootstrap()).thenReturn(bootstrap);
        Registries.PARTICLES.load();
    }

    private static ParticleType unmapped() {
        for (ParticleType type : ParticleType.values()) {
            if (Registries.PARTICLES.get(type) == null) {
                return type;
            }
        }
        throw new AssertionError("every particle type is mapped, test needs a miss");
    }

    private static ClientboundLevelParticlesPacket packet(ParticleType type) {
        return new ClientboundLevelParticlesPacket(new Particle(type, null), false, false, 1.5, 64.0, -3.25,
            0.1f, 0.2f, 0.3f, 0.0f, 0.0f, 0.0f, 0, ClientboundLevelParticlesPacket.RandomizationType.ALTERNATIVE);
    }

    @Test
    void missSendsNothingAndSkipsToStringWithoutDebug() {
        mockContext(() -> {
            loadParticles();
            GeyserLogger logger = mock(GeyserLogger.class);
            when(logger.isDebug()).thenReturn(false);
            GeyserSession session = mock(GeyserSession.class, Mockito.RETURNS_DEEP_STUBS);
            when(session.getGeyser().getLogger()).thenReturn(logger);
            ClientboundLevelParticlesPacket pkt = Mockito.spy(packet(unmapped()));

            new JavaLevelParticlesTranslator().translate(session, pkt);

            verify(session, never()).sendUpstreamPacket(Mockito.any());
            verify(pkt, never()).getAmount(); // the mapped branch never ran
            verify(logger, never()).debug(anyString());
        });
    }

    @Test
    void missStillLogsWithDebugOn() {
        mockContext(() -> {
            loadParticles();
            GeyserLogger logger = mock(GeyserLogger.class);
            when(logger.isDebug()).thenReturn(true);
            GeyserSession session = mock(GeyserSession.class, Mockito.RETURNS_DEEP_STUBS);
            when(session.getGeyser().getLogger()).thenReturn(logger);
            ClientboundLevelParticlesPacket pkt = packet(unmapped());

            new JavaLevelParticlesTranslator().translate(session, pkt);

            verify(logger).debug("Unhandled particle packet: " + pkt);
        });
    }

    @Test
    void avoidedStringCostIsMeasured() {
        mockContext(() -> {
            loadParticles();
            ClientboundLevelParticlesPacket pkt = packet(unmapped());
            com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
            long id = Thread.currentThread().getId();
            int n = 20000;
            for (int i = 0; i < 40000; i++) { if (("Unhandled particle packet: " + pkt).isEmpty()) throw new AssertionError(); }
            long best = Long.MAX_VALUE;
            for (int r = 0; r < 5; r++) {
                long t0 = mx.getThreadAllocatedBytes(id);
                for (int i = 0; i < n; i++) { if (("Unhandled particle packet: " + pkt).isEmpty()) throw new AssertionError(); }
                best = Math.min(best, (mx.getThreadAllocatedBytes(id) - t0) / n);
            }
            // This is the work the debug guard removes from every unmapped-particle packet when debug logging is off
            System.out.println("PERF particle miss avoided string bytes/call=" + best);
            assertTrue(best > 0);
        });
    }
}
