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

package org.geysermc.geyser.network.bedrock;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.data.CompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.BatchCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionStrategy;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.NoopCompression;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.SimpleCompressionStrategy;

/**
 * Skips deflating small batches, matching the compression threshold advertised in {@code NetworkSettingsPacket}.
 *
 * <p>A non-default compression algorithm (such as {@link NoopCompression}) may only be returned when the
 * {@code CompressionCodec} prefixes batches with an algorithm header. Without that prefix the codec rejects any
 * algorithm that differs from {@link #getDefaultCompression()}. {@code BedrockPeer.setCompression} builds the codec
 * with {@code prefixed = protocolVersion >= 649}, which holds for every supported Bedrock version
 * on both RakNet and NetherNet, so the threshold is safe to apply for both transports. The flag is still carried on
 * the strategy so that no throw is possible if it were ever used with an unprefixed codec.</p>
 */
public class ThresholdCompressionStrategy implements CompressionStrategy {
    private static final int THRESHOLD = 512;

    private final SimpleCompressionStrategy delegate;
    private final NoopCompression noop;
    private final boolean prefixed;

    public ThresholdCompressionStrategy(SimpleCompressionStrategy delegate, boolean prefixed) {
        this.delegate = delegate;
        this.noop = new NoopCompression();
        this.prefixed = prefixed;
    }

    @Override
    public BatchCompression getCompression(BedrockBatchWrapper batch) {
        if (prefixed) {
            ByteBuf uncompressed = batch.getUncompressed();
            if (uncompressed != null && uncompressed.readableBytes() < THRESHOLD) {
                return noop;
            }
        }
        return delegate.getCompression(batch);
    }

    @Override
    public BatchCompression getCompression(CompressionAlgorithm algorithm) {
        return delegate.getCompression(algorithm);
    }

    @Override
    public BatchCompression getDefaultCompression() {
        return delegate.getDefaultCompression();
    }

    public boolean isPrefixed() {
        return prefixed;
    }
}
