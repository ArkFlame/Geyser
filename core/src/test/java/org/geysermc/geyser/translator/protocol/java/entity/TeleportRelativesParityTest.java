package org.geysermc.geyser.translator.protocol.java.entity;

import org.cloudburstmc.math.vector.Vector3d;
import org.cloudburstmc.math.vector.Vector3f;
import org.geysermc.geyser.entity.type.Entity;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.util.MathUtils;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PositionElement;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundTeleportEntityPacket;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Single pass over the relatives must behave exactly like the old per-flag List#contains logic, for every subset. */
class TeleportRelativesParityTest {

    @Test
    void everyRelativeSubsetMatchesOldLogic() {
        PositionElement[] all = PositionElement.values();
        Random r = new Random(5);
        for (int mask = 0; mask < (1 << all.length); mask++) {
            List<PositionElement> rel = new ArrayList<>();
            for (int i = 0; i < all.length; i++) if ((mask & (1 << i)) != 0) rel.add(all[i]);

            Vector3f entityPos = Vector3f.from(r.nextInt(100) - 50, r.nextInt(100) - 50, r.nextInt(100) - 50);
            Vector3f motion = Vector3f.from(r.nextFloat(), r.nextFloat(), r.nextFloat());
            float pitch = r.nextInt(180) - 90, yaw = r.nextInt(360) - 180;
            Vector3d pos = Vector3d.from(r.nextInt(20), r.nextInt(20), r.nextInt(20));
            Vector3d delta = Vector3d.from(r.nextDouble(), r.nextDouble(), r.nextDouble());
            float xRot = r.nextInt(100) - 50, yRot = r.nextInt(100) - 50;

            GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
            Entity entity = mock(Entity.class, RETURNS_DEEP_STUBS);
            when(session.getEntityCache().getEntityByJavaId(9)).thenReturn(entity);
            when(entity.position()).thenReturn(entityPos);
            when(entity.getMotion()).thenReturn(motion);
            when(entity.getPitch()).thenReturn(pitch);
            when(entity.getYaw()).thenReturn(yaw);
            when(entity.interpolatesTeleports()).thenReturn(false);
            when(entity.getPassengers()).thenReturn(new ArrayList<>());

            // old algorithm
            Vector3d expPos = pos.add(
                rel.contains(PositionElement.X) ? entityPos.getX() : 0,
                rel.contains(PositionElement.Y) ? entityPos.getY() : 0,
                rel.contains(PositionElement.Z) ? entityPos.getZ() : 0);
            boolean hasRelative = rel.contains(PositionElement.X) || rel.contains(PositionElement.Y) || rel.contains(PositionElement.Z);
            boolean interpolate = hasRelative && entityPos.distance(expPos.toFloat()) < 4096.0;
            float newPitch = MathUtils.clamp(xRot + (rel.contains(PositionElement.X_ROT) ? pitch : 0), -90, 90);
            float newYaw = yRot + (rel.contains(PositionElement.Y_ROT) ? yaw : 0);
            Vector3f expMotion = delta.toFloat().add(
                rel.contains(PositionElement.DELTA_X) ? motion.getX() : 0,
                rel.contains(PositionElement.DELTA_Y) ? motion.getY() : 0,
                rel.contains(PositionElement.DELTA_Z) ? motion.getZ() : 0);
            if (rel.contains(PositionElement.ROTATE_DELTA)) {
                expMotion = MathUtils.xYRot(expMotion, (float) Math.toRadians(pitch - newPitch), (float) Math.toRadians(yaw - newYaw));
            }

            new JavaTeleportEntityTranslator().translate(session, new ClientboundTeleportEntityPacket(9, pos, delta, yRot, xRot, rel, true));

            if (interpolate) {
                Vector3d cur = entityPos.toDouble();
                verify(entity).moveRelative(expPos.getX() - cur.getX(), expPos.getY() - cur.getY(), expPos.getZ() - cur.getZ(), newYaw, newPitch, newYaw, true);
            } else {
                verify(entity).teleport(expPos.toFloat(), newYaw, newPitch, true);
            }
            ArgumentCaptor<Vector3f> captor = ArgumentCaptor.forClass(Vector3f.class);
            verify(entity).setMotion(captor.capture());
            assertEquals(expMotion, captor.getValue(), "mask=" + mask);
        }
    }
}
