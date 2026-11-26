package org.geysermc.geyser.session;

import io.netty.channel.DefaultEventLoop;
import org.geysermc.geyser.session.cache.ChunkCache;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** After disconnect the session must not keep its cached chunks reachable (GeyserMC/Geyser#6920 retention). */
class DisconnectReleasesChunkCacheTest {

    private static void set(Object o, String name, Object v) throws Exception {
        Field f = GeyserSession.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(o, v);
    }

    @Test
    void chunkCacheIsEmptyAfterDisconnect() throws Exception {
        GeyserSession helper = mock(GeyserSession.class, RETURNS_DEEP_STUBS);
        ChunkCache cache = new ChunkCache(helper);
        assertEquals(true, cache.isCache());
        for (int i = 0; i < 50; i++) {
            cache.addToCache(i, -i, new org.geysermc.mcprotocollib.protocol.data.game.chunk.DataPalette[0]);
        }
        assertEquals(50, chunkCount(cache), "precondition: cache holds chunks");

        DefaultEventLoop loop = new DefaultEventLoop();
        try {
            GeyserSession session = mock(GeyserSession.class, CALLS_REAL_METHODS);
            set(session, "chunkCache", cache);
            set(session, "tickEventLoop", loop);
            set(session, "serverSuggestionCache", mock(org.geysermc.geyser.session.cache.ServerSuggestionCache.class));
            set(session, "queuedImmediatelyPackets", new ArrayList<>());
            set(session, "erosionHandler", mock(org.geysermc.geyser.erosion.AbstractGeyserboundPacketHandler.class));
            set(session, "geyser", mock(org.geysermc.geyser.GeyserImpl.class, RETURNS_DEEP_STUBS));
            set(session, "sharedRegistryHandle", mock(org.geysermc.geyser.session.shared.SessionSharedCache.Handle.class));
            var player = mock(org.geysermc.geyser.entity.type.player.SessionPlayerEntity.class);
            when(player.uuid()).thenReturn(null); // skips the SkinProvider cleanup, whose static init needs a running Geyser
            set(session, "playerEntity", player);
            Object upstream = mock(UpstreamSession.class, RETURNS_DEEP_STUBS);
            set(session, "upstream", upstream);

            Field ds = GeyserSession.class.getDeclaredField("downstream");
            ds.setAccessible(true);
            ds.set(session, mock(ds.getType(), RETURNS_DEEP_STUBS)); // a connected downstream skips the locale log path

            session.disconnect("test");
            loop.submit(() -> { }).sync(); // runs after the queued clear (FIFO)

            assertEquals(0, chunkCount(cache), "chunk cache must be released on disconnect");
        } finally {
            loop.shutdownGracefully();
        }
    }

    private static int chunkCount(ChunkCache cache) throws Exception {
        Field f = ChunkCache.class.getDeclaredField("chunks");
        f.setAccessible(true);
        return ((it.unimi.dsi.fastutil.longs.Long2ObjectMap<?>) f.get(cache)).size();
    }
}
