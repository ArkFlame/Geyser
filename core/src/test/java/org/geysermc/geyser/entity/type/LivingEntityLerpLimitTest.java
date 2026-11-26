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

package org.geysermc.geyser.entity.type;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.geysermc.geyser.session.GeyserSession;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * Each lerping living entity turns one Java move into up to three Bedrock move packets spread over the following
 * ticks. With hundreds of mobs loaded that is thousands of packets per second and console clients stall (#6129), so
 * past a tickable-entity budget Geyser sends the single direct move instead.
 */
class LivingEntityLerpLimitTest {
    private static LivingEntity entityInASessionTicking(int tickable) throws Exception {
        GeyserSession session = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
        List<Tickable> list = new ObjectArrayList<>();
        for (int i = 0; i < tickable; i++) {
            list.add(mock(Tickable.class));
        }
        when(session.getEntityCache().getTickableEntities()).thenReturn(list);
        when(session.isInClientPredictedVehicle()).thenReturn(false);

        LivingEntity entity = mock(LivingEntity.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        Field field = Entity.class.getDeclaredField("session");
        field.setAccessible(true);
        field.set(entity, session);
        return entity;
    }

    @Test
    void anEntityLerpsWhileTheTickableCountIsWithinTheBudget() throws Exception {
        assertTrue(entityInASessionTicking(0).shouldLerp());
        assertTrue(entityInASessionTicking(100).shouldLerp());
    }

    @Test
    void anEntityStopsLerpingOnceTheBudgetIsExceeded() throws Exception {
        assertFalse(entityInASessionTicking(101).shouldLerp());
        assertFalse(entityInASessionTicking(1136).shouldLerp());
    }
}
