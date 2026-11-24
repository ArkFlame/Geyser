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

package org.geysermc.geyser.session.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A cache of immutable values that can be shared across sessions.
 *
 * <p>Keys must be immutable and have value equality, and values returned by {@link Handle#getOrCompute(Object, Supplier)}
 * must be immutable: they are handed out to every session that asks for the same key. A value is kept for as long as at
 * least one {@link Handle} holds it; {@link Handle#releaseAll()} drops all values held by one session.</p>
 *
 * <p>Caches are owned by a {@link org.geysermc.geyser.GeyserImpl} instance rather than by a static singleton, so a reload
 * cannot leave values behind for the next instance.</p>
 *
 * @param <K> the type of the cache key
 * @param <V> the type of the immutable cached value
 */
public final class SessionSharedCache<K, V> {
    private final Map<K, SharedValue<V>> values = new HashMap<>();

    /**
     * Creates a handle for a single session. The handle must be released when that session ends.
     */
    public Handle<K, V> newHandle() {
        return new Handle<>(this);
    }

    private synchronized V retainOrCompute(K key, Supplier<? extends V> computer) {
        SharedValue<V> value = values.get(key);
        if (value == null) {
            value = new SharedValue<>(Objects.requireNonNull(computer.get(), "Shared cache values may not be null"));
            values.put(key, value);
        }
        value.references++;
        return value.value;
    }

    private synchronized void release(K key) {
        SharedValue<V> value = values.get(key);
        if (value != null && --value.references == 0) {
            values.remove(key);
        }
    }

    /**
     * A session's view of a {@link SessionSharedCache}. Tracks exactly which keys that session has retained.
     */
    public static final class Handle<K, V> {
        private final SessionSharedCache<K, V> cache;
        private final Map<K, V> held = new HashMap<>();

        private Handle(SessionSharedCache<K, V> cache) {
            this.cache = cache;
        }

        /**
         * Returns the cached value for {@code key}, computing and sharing it on the first request. Repeated calls with
         * the same key only retain the value once.
         */
        public synchronized V getOrCompute(K key, Supplier<? extends V> computer) {
            V value = held.get(key);
            if (value == null) {
                value = cache.retainOrCompute(key, computer);
                held.put(key, value);
            }
            return value;
        }

        /**
         * Releases everything this session held. Safe to call more than once; the handle can be reused afterwards.
         */
        public synchronized void releaseAll() {
            for (K key : held.keySet()) {
                cache.release(key);
            }
            held.clear();
        }
    }

    private static final class SharedValue<V> {
        private final V value;
        private int references;

        private SharedValue(V value) {
            this.value = value;
        }
    }
}
