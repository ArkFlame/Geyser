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

package org.geysermc.geyser.debug;

import org.geysermc.geyser.session.GeyserSession;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class GeyserDebugSessionMap {
    private final Map<Long, EnumSet<SessionDebugOption>> debugOptionsHolder = new HashMap<>();

    public boolean toggleDebugOption(long xuid, SessionDebugOption option) {
        EnumSet<SessionDebugOption> set = debugOptionsHolder.computeIfAbsent(xuid, $ -> EnumSet.noneOf(SessionDebugOption.class));
        if (set.remove(option)) {
            return false;
        }
        set.add(option);
        return true;
    }

    /**
     * Applies the debug options collected for this xuid to the given session. Does nothing when the
     * session has no xuid, or when no options are pending for it.
     */
    public void setFor(GeyserSession session) {
        String xuid = session.xuid();
        if (xuid == null || xuid.isEmpty()) {
            return;
        }
        EnumSet<SessionDebugOption> options;
        try {
            options = debugOptionsHolder.get(Long.parseLong(xuid));
        } catch (NumberFormatException e) {
            return;
        }
        if (options != null) {
            session.setDebugOptions(EnumSet.copyOf(options));
        }
    }
}
