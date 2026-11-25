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

package org.geysermc.geyser.network.bedrock.nethernet;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.cloudburstmc.netty.channel.nethernet.AddressSource;
import org.cloudburstmc.netty.channel.nethernet.NetherNetChannel;
import org.cloudburstmc.netty.channel.nethernet.SourcedAddress;

import java.net.InetSocketAddress;

/**
 * Picks the player address for a NetherNet connection. Built-in signaling reads a PROXY protocol v2
 * header from its trusted proxies; the WebRTC path behind a TCP+UDP tunnel may only see the tunnel,
 * so the network library's reported (media) address can be the forwarder instead of the player.
 */
public final class NetherNetAddresses {
    private NetherNetAddresses() {
    }

    /**
     * The address to attribute the connection to, preferring one a trusted proxy delivered over signaling.
     */
    public static SourcedAddress clientAddress(NetherNetChannel channel) {
        SourcedAddress signaled = channel.signaledAddress();
        if (isSignalingProxyClient(signaled)) {
            return signaled;
        }
        SourcedAddress reported = channel.reportedAddress();
        return reported != null ? reported : signaled;
    }

    /**
     * The client address a trusted PROXY protocol header gave signaling, or null when there is none.
     * Only a real address is usable: a wildcard one says the proxy did not send a client address.
     */
    public static @Nullable InetSocketAddress signalingProxyClient(NetherNetChannel channel) {
        SourcedAddress signaled = channel.signaledAddress();
        return isSignalingProxyClient(signaled) ? signaled.address() : null;
    }

    private static boolean isSignalingProxyClient(@Nullable SourcedAddress signaled) {
        if (signaled == null || signaled.source() != AddressSource.SIGNALING_PROXY_PROTOCOL) {
            return false;
        }
        InetSocketAddress address = signaled.address();
        return address != null && address.getAddress() != null && !address.getAddress().isAnyLocalAddress();
    }
}
