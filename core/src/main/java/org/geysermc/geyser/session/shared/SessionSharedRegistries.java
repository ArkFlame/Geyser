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

import org.geysermc.geyser.registry.java.JavaRegistryKey;

import java.util.function.Supplier;

/**
 * Shares parsed values of inline registry entries. Entries are keyed by the registry and the raw NBT data itself (not
 * its hash code), so two different entries can never be mistaken for one another.
 *
 * <p>Only readers explicitly marked as stateless in {@link org.geysermc.geyser.registry.java.reader.JavaRegistryReaders}
 * may be shared: their output depends solely on the raw data, never on a particular session's other registries.</p>
 */
public final class SessionSharedRegistries {
    private final SessionSharedCache<RegistryDataKey<?>, Object> cache = new SessionSharedCache<>();

    public SessionSharedCache.Handle<RegistryDataKey<?>, Object> newHandle() {
        return cache.newHandle();
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCompute(SessionSharedCache.Handle<RegistryDataKey<?>, Object> handle,
                              JavaRegistryKey<T> registry, Object data, Supplier<T> computer) {
        return (T) handle.getOrCompute(new RegistryDataKey<>(registry, data), computer);
    }

    /**
     * @param registry the registry the entry belongs to
     * @param data the immutable raw NBT data of the entry
     * @param <T> the type of the registry
     */
    public record RegistryDataKey<T>(JavaRegistryKey<T> registry, Object data) {}
}
