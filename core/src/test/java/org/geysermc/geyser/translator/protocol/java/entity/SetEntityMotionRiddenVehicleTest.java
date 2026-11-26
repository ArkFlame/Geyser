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

package org.geysermc.geyser.translator.protocol.java.entity;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket;
import org.geysermc.geyser.entity.type.Entity;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundSetEntityMotionPacket;
import org.junit.jupiter.api.Test;
import org.cloudburstmc.math.vector.Vector3d;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Java pushes velocity onto an entity the player rides. Bedrock applies a SetEntityMotionPacket to its linked rider's
 * vehicle client-side while Geyser also streams the authoritative position, so the rider's view stops following the
 * mount (#5911). Geyser already suppressed this for horses; it applies to any ridden vehicle.
 */
class SetEntityMotionRiddenVehicleTest {
    private static final ClientboundSetEntityMotionPacket MOTION = new ClientboundSetEntityMotionPacket(5, Vector3d.from(0.0, 0.4, 0.2));

    private final JavaSetEntityMotionTranslator translator = new JavaSetEntityMotionTranslator();
    private final GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
    private final Entity entity = mock(Entity.class, RETURNS_DEEP_STUBS);

    private void entityWithId() {
        when(session.getEntityCache().getEntityByJavaId(5)).thenReturn(entity);
        when(entity.getMotion()).thenReturn(Vector3f.from(0, 0.4f, 0.2f));
    }

    @Test
    void motionIsNotSentForAnyEntityThePlayerIsRiding() {
        entityWithId();
        when(session.getPlayerEntity().getVehicle()).thenReturn(entity);

        translator.translate(session, MOTION);

        verify(session, never()).sendUpstreamPacket(any(SetEntityMotionPacket.class));
        // The server-side state is still kept up to date.
        verify(entity).setMotion(any(Vector3f.class));
    }

    @Test
    void motionIsStillSentForAnEntityThePlayerIsNotRiding() {
        entityWithId();
        when(session.getPlayerEntity().getVehicle()).thenReturn(null);

        translator.translate(session, MOTION);

        verify(session).sendUpstreamPacket(any(SetEntityMotionPacket.class));
    }
}
