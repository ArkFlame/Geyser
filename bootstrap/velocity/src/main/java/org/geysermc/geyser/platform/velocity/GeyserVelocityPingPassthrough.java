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

package org.geysermc.geyser.platform.velocity;

import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.network.HandshakeIntent;
import com.velocitypowered.api.network.ProtocolState;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.proxy.server.ServerPing.Version;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.geyser.network.bedrock.GameProtocol;
import org.geysermc.geyser.ping.GeyserPingInfo;
import org.geysermc.geyser.ping.IGeyserPingPassthrough;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
public class GeyserVelocityPingPassthrough implements IGeyserPingPassthrough {

    private final ProxyServer server;

    @Override
    public GeyserPingInfo getPingInformation(InetSocketAddress inetSocketAddress) {
        return getPingInformation(inetSocketAddress, null);
    }

    @Override
    public GeyserPingInfo getPingInformation(InetSocketAddress inetSocketAddress, @Nullable InetSocketAddress virtualHost) {
        ProxyPingEvent event;
        try {
            event = server.getEventManager().fire(new ProxyPingEvent(new GeyserInboundConnection(inetSocketAddress, resolveVirtualHost(virtualHost)), ServerPing.builder()
                .description(server.getConfiguration().getMotd())
                .onlinePlayers(server.getPlayerCount())
                .maximumPlayers(server.getConfiguration().getShowMaxPlayers())
                .version(new Version(GameProtocol.getJavaProtocolVersion(), GameProtocol.getJavaMinecraftVersion())).build())).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new GeyserPingInfo(
                GsonComponentSerializer.gson().serialize(event.getPing().getDescriptionComponent()),
                event.getPing().getPlayers().map(ServerPing.Players::getMax).orElse(1),
                event.getPing().getPlayers().map(ServerPing.Players::getOnline).orElse(0)
        );
    }

    /**
     * A Bedrock client can ping without a host or with a bare IP (NetherNet clients do), which leaves
     * Velocity unable to resolve a registered server and makes it answer with its own MOTD. Use the first
     * forced host, or else the address of the server Velocity would join first, so ping passthrough still
     * reaches the Java server. A real hostname is used unchanged.
     */
    private InetSocketAddress resolveVirtualHost(@Nullable InetSocketAddress virtualHost) {
        if (virtualHost != null && !isBareAddress(virtualHost.getHostString())) {
            return virtualHost;
        }

        for (String forcedHost : server.getConfiguration().getForcedHosts().keySet()) {
            if (!forcedHost.isBlank()) {
                return parseHost(forcedHost);
            }
        }

        for (String serverName : server.getConfiguration().getAttemptConnectionOrder()) {
            Optional<RegisteredServer> registered = server.getServer(serverName);
            if (registered.isPresent()) {
                return registered.get().getServerInfo().getAddress();
            }
        }

        return virtualHost;
    }

    private static boolean isBareAddress(@Nullable String host) {
        if (host == null || host.isBlank()) {
            return true;
        }
        if (host.indexOf(':') >= 0) {
            // IPv6 literal, with or without brackets.
            return true;
        }
        String[] parts = host.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            if (part.isEmpty() || part.length() > 3) {
                return false;
            }
            for (int i = 0; i < part.length(); i++) {
                if (!Character.isDigit(part.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private InetSocketAddress parseHost(String host) {
        String value = host.trim();
        int port = server.getBoundAddress().getPort();

        if (value.startsWith("[")) {
            int close = value.indexOf(']');
            if (close > 0) {
                if (value.length() > close + 1 && value.charAt(close + 1) == ':') {
                    port = parsePort(value.substring(close + 2), port);
                }
                return InetSocketAddress.createUnresolved(value.substring(1, close), port);
            }
        }

        int colon = value.lastIndexOf(':');
        if (colon > 0 && value.indexOf(':') == colon) {
            port = parsePort(value.substring(colon + 1), port);
            value = value.substring(0, colon);
        }
        return InetSocketAddress.createUnresolved(value, port);
    }

    private static int parsePort(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static class GeyserInboundConnection implements InboundConnection {

        private final InetSocketAddress remote;
        private final @Nullable InetSocketAddress virtualHost;

        public GeyserInboundConnection(InetSocketAddress remote, @Nullable InetSocketAddress virtualHost) {
            this.remote = remote;
            this.virtualHost = virtualHost;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return this.remote;
        }

        @Override
        public Optional<InetSocketAddress> getVirtualHost() {
            return Optional.ofNullable(this.virtualHost);
        }

        @Override
        public Optional<String> getRawVirtualHost() {
            return Optional.ofNullable(this.virtualHost).map(InetSocketAddress::getHostString);
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public ProtocolVersion getProtocolVersion() {
            return ProtocolVersion.MAXIMUM_VERSION;
        }

        @Override
        public ProtocolState getProtocolState() {
            return ProtocolState.STATUS;
        }

        @Override
        public HandshakeIntent getHandshakeIntent() {
            return HandshakeIntent.STATUS;
        }
    }

}
