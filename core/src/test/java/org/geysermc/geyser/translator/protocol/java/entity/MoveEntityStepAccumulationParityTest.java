package org.geysermc.geyser.translator.protocol.java.entity;

import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.geyser.entity.type.Entity;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosRotPacket;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The plain-double accumulation must be bit-identical to the old Vector3d accumulation, including signed zeros. */
class MoveEntityStepAccumulationParityTest {

    private static Vector3d oldSum(List<double[]> steps) {
        Vector3d move = Vector3d.ZERO;
        for (double[] s : steps) {
            move = move.add(s[0], s[1], s[2]);
        }
        return move;
    }

    private static List<double[]> randomSteps(Random r, int n) {
        List<double[]> out = new ArrayList<>();
        double[] specials = {0.0, -0.0, 1 / 4096D, -1 / 4096D, 8.0, -8.0};
        for (int i = 0; i < n; i++) {
            double[] s = new double[3];
            for (int j = 0; j < 3; j++) {
                s[j] = r.nextInt(5) == 0 ? specials[r.nextInt(specials.length)] : (r.nextInt(65536) - 32768) / 4096D;
            }
            out.add(s);
        }
        return out;
    }

    @Test
    void posStepsAndPlain() {
        Random r = new Random(11);
        for (int iter = 0; iter < 500; iter++) {
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            Entity entity = mock(Entity.class, RETURNS_DEEP_STUBS);
            when(session.getEntityCache().getEntityByJavaId(3)).thenReturn(entity);
            when(entity.getYaw()).thenReturn(1f);
            when(entity.getPitch()).thenReturn(2f);
            when(entity.getHeadYaw()).thenReturn(3f);

            boolean plain = iter % 5 == 0;
            List<double[]> steps = randomSteps(r, 1 + r.nextInt(6));
            ClientboundMoveEntityPosPacket packet;
            Vector3d expected;
            if (plain) {
                double[] s = steps.get(0);
                packet = new ClientboundMoveEntityPosPacket(3, true, 0, null, s[0], s[1], s[2]);
                expected = Vector3d.from(s[0], s[1], s[2]);
            } else {
                List<ClientboundMoveEntityPosPacket.DeltaStep> list = new ArrayList<>();
                for (double[] s : steps) list.add(new ClientboundMoveEntityPosPacket.DeltaStep(1, s[0], s[1], s[2]));
                packet = new ClientboundMoveEntityPosPacket(3, true, list.size(), list, 0, 0, 0);
                expected = oldSum(steps);
            }
            new JavaMoveEntityPosTranslator().translate(session, packet);
            verify(entity).moveRelative(expected.getX(), expected.getY(), expected.getZ(), 1f, 2f, 3f, true);
        }
    }

    @Test
    void posRotStepsAndPlain() {
        Random r = new Random(12);
        for (int iter = 0; iter < 500; iter++) {
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            Entity entity = mock(Entity.class, RETURNS_DEEP_STUBS);
            when(session.getEntityCache().getEntityByJavaId(3)).thenReturn(entity);

            boolean plain = iter % 5 == 0;
            List<double[]> steps = randomSteps(r, 1 + r.nextInt(6));
            ClientboundMoveEntityPosRotPacket packet;
            Vector3d expected;
            if (plain) {
                double[] s = steps.get(0);
                packet = new ClientboundMoveEntityPosRotPacket(3, false, 0, null, s[0], s[1], s[2], 10f, 20f);
                expected = Vector3d.from(s[0], s[1], s[2]);
            } else {
                List<ClientboundMoveEntityPosRotPacket.DeltaStep> list = new ArrayList<>();
                for (double[] s : steps) list.add(new ClientboundMoveEntityPosRotPacket.DeltaStep(1, s[0], s[1], s[2]));
                packet = new ClientboundMoveEntityPosRotPacket(3, false, list.size(), list, 0, 0, 0, 10f, 20f);
                expected = oldSum(steps);
            }
            new JavaMoveEntityPosRotTranslator().translate(session, packet);
            verify(entity).updatePositionAndRotation(expected.getX(), expected.getY(), expected.getZ(), 10f, 20f, false);
        }
    }
}
