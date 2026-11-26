package org.geysermc.geyser.entity.type;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket;
import org.geysermc.geyser.session.GeyserSession;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/** moveRelativeRaw computes bedrockPosition() once; the packet must equal what the old triple-call code produced. */
class EntityMoveRelativeParityTest {

    private static void set(Object o, String name, Object v) throws Exception {
        Field f = Entity.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(o, v);
    }

    @Test
    void packetMatchesOldBehaviour() throws Exception {
        Random r = new Random(3);
        double[] deltas = {0.0, -0.0, 0.5, -1.25, 4.0};
        for (int i = 0; i < 400; i++) {
            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            Entity e = mock(Entity.class, CALLS_REAL_METHODS);
            Vector3f pos = Vector3f.from(r.nextInt(50), r.nextInt(50), r.nextInt(50));
            float offset = r.nextBoolean() ? 0f : 1.62f;
            float oldYaw = 10f, oldPitch = 20f, oldHead = 30f;
            set(e, "session", session);
            set(e, "position", pos);
            set(e, "offset", offset);
            set(e, "valid", true);
            set(e, "geyserId", 77L);
            set(e, "yaw", oldYaw);
            set(e, "pitch", oldPitch);
            set(e, "headYaw", oldHead);

            double rx = deltas[r.nextInt(deltas.length)], ry = deltas[r.nextInt(deltas.length)], rz = deltas[r.nextInt(deltas.length)];
            float yaw = r.nextBoolean() ? oldYaw : 11f, pitch = r.nextBoolean() ? oldPitch : 21f, head = r.nextBoolean() ? oldHead : 31f;
            boolean ground = r.nextBoolean();

            e.moveRelativeRaw(rx, ry, rz, yaw, pitch, head, ground);

            Vector3f newPos = pos.add((float) rx, (float) ry, (float) rz);
            Vector3f bedrock = offset == 0f ? newPos : newPos.up(offset);
            MoveEntityDeltaPacket expected = new MoveEntityDeltaPacket();
            expected.setRuntimeEntityId(77L);
            if (rx != 0.0) { expected.setX(bedrock.getX()); expected.getFlags().add(MoveEntityDeltaPacket.Flag.HAS_X); }
            if (ry != 0.0) { expected.setY(bedrock.getY()); expected.getFlags().add(MoveEntityDeltaPacket.Flag.HAS_Y); }
            if (rz != 0.0) { expected.setZ(bedrock.getZ()); expected.getFlags().add(MoveEntityDeltaPacket.Flag.HAS_Z); }
            if (pitch != oldPitch) { expected.setPitch(pitch); expected.getFlags().add(MoveEntityDeltaPacket.Flag.HAS_PITCH); }
            if (yaw != oldYaw) { expected.setYaw(yaw); expected.getFlags().add(MoveEntityDeltaPacket.Flag.HAS_YAW); }
            if (head != oldHead) { expected.setHeadYaw(head); expected.getFlags().add(MoveEntityDeltaPacket.Flag.HAS_HEAD_YAW); }
            expected.setOnGround(ground);
            if (ground) expected.getFlags().add(MoveEntityDeltaPacket.Flag.ON_GROUND);

            ArgumentCaptor<MoveEntityDeltaPacket> captor = ArgumentCaptor.forClass(MoveEntityDeltaPacket.class);
            verify(session).sendUpstreamPacket(captor.capture());
            assertEquals(expected, captor.getValue(), "iteration " + i);
        }
    }
}
