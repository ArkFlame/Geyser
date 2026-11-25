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

import org.cloudburstmc.netty.channel.nethernet.AddressSource;
import org.cloudburstmc.netty.channel.nethernet.NetherNetChannel;
import org.cloudburstmc.netty.channel.nethernet.SourcedAddress;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NetherNetProxyAddressTest {
    @Test
    void proxyProtocolClientBeatsMediaRelay() {
        InetSocketAddress client = new InetSocketAddress("198.51.100.7", 40000);
        InetSocketAddress relay = new InetSocketAddress("172.18.0.1", 0);
        NetherNetChannel channel = mock(NetherNetChannel.class);
        SourcedAddress signaled = new SourcedAddress(client, AddressSource.SIGNALING_PROXY_PROTOCOL,
            new InetSocketAddress("172.18.0.1", 1234));
        when(channel.signaledAddress()).thenReturn(signaled);
        when(channel.reportedAddress()).thenReturn(new SourcedAddress(relay, AddressSource.ICE, null));

        assertEquals(client, NetherNetAddresses.signalingProxyClient(channel));
        assertSame(signaled, NetherNetAddresses.clientAddress(channel));
    }

    @Test
    void withoutProxyHeaderTheReportedAddressIsUnchanged() {
        NetherNetChannel channel = mock(NetherNetChannel.class);
        when(channel.signaledAddress()).thenReturn(new SourcedAddress(
            new InetSocketAddress("198.51.100.7", 40000), AddressSource.SIGNALING, null));
        SourcedAddress reported = new SourcedAddress(new InetSocketAddress("198.51.100.7", 40001), AddressSource.ICE, null);
        when(channel.reportedAddress()).thenReturn(reported);

        assertNull(NetherNetAddresses.signalingProxyClient(channel));
        assertSame(reported, NetherNetAddresses.clientAddress(channel));
    }

    @Test
    void wildcardProxyAddressIsNotAttributedToThePlayer() {
        NetherNetChannel channel = mock(NetherNetChannel.class);
        when(channel.signaledAddress()).thenReturn(new SourcedAddress(
            new InetSocketAddress("0.0.0.0", 0), AddressSource.SIGNALING_PROXY_PROTOCOL, null));
        SourcedAddress reported = new SourcedAddress(new InetSocketAddress("172.18.0.1", 0), AddressSource.ICE, null);
        when(channel.reportedAddress()).thenReturn(reported);

        assertNull(NetherNetAddresses.signalingProxyClient(channel));
        assertSame(reported, NetherNetAddresses.clientAddress(channel));
    }
}
