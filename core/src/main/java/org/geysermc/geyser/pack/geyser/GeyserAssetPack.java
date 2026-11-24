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

package org.geysermc.geyser.pack.geyser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.GeyserLogger;
import org.geysermc.geyser.configuration.GeyserConfigDirectory;
import org.geysermc.geyser.item.exception.InvalidCustomMappingsFileException;
import org.geysermc.geyser.registry.loader.ResourcePackLoader;
import org.geysermc.geyser.registry.mappings.util.MappingsUtil;
import org.geysermc.geyser.registry.mappings.util.NodeReader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.ProviderNotFoundException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A server-supplied {@code .geyserpack} archive. Packs can provide resource packs, custom mappings and
 * locale overrides. Loading only happens during startup or a Geyser reload - never on a session thread.
 */
public record GeyserAssetPack(Manifest manifest, Path root) {
    public static final String EXTENSION = ".geyserpack";

    private static final String MANIFEST_PATH = "geyser_manifest.json";
    private static final String JSON_GLOB = "**.json";

    // Hard limits for untrusted archives. Manifests are tiny, and a pack is not meant to be a huge download.
    private static final long MAX_MANIFEST_SIZE = 64L * 1024;
    private static final long MAX_ENTRY_SIZE = 16L * 1024 * 1024;
    private static final long MAX_TOTAL_SIZE = 128L * 1024 * 1024;
    private static final int MAX_ENTRIES = 4096;

    public void resourcePacks(Consumer<Path> consumer) {
        listDirectory("packs", ResourcePackLoader.PACK_GLOB, consumer);
    }

    public void mappings(Consumer<Path> consumer) {
        listDirectory("mappings", JSON_GLOB, consumer);
    }

    public void locale(String locale, Consumer<Path> consumer) {
        listDirectory("locales", locale + ".json", consumer);
    }

    private void listDirectory(String directory, String glob, Consumer<Path> consumer) {
        Path path = root.resolve(directory);
        if (!Files.isDirectory(path)) {
            return;
        }

        try (DirectoryStream<Path> files = Files.newDirectoryStream(path, glob)) {
            files.forEach(consumer);
        } catch (IOException exception) {
            GeyserImpl.getInstance().getLogger().error("Failed to read files in " + path + "!", exception);
        }
    }

    public static List<GeyserAssetPack> load(GeyserImpl geyser) {
        // Opt-in: a server administrator must explicitly enable loading these archives.
        if (!geyser.config().advanced().loadGeyserPacks()) {
            return List.of();
        }
        return load(geyser.configDir(), geyser.getLogger());
    }

    public static List<GeyserAssetPack> load(GeyserConfigDirectory configDir, GeyserLogger logger) {
        Path cache = configDir.createIfAbsent(GeyserConfigDirectory.CACHE_GEYSER_PACKS);
        Path packsDirectory = configDir.createIfAbsent(GeyserConfigDirectory.PACKS);

        Set<Pending> pendingPacks = new ObjectOpenHashSet<>();
        try (Stream<Path> packs = Files.list(packsDirectory)) {
            packs.filter(GeyserAssetPack::isGeyserAssetPack)
                .flatMap(pack -> tryLoad(pack, logger))
                .sorted(Pending.COMPARATOR)
                .forEach(pack -> {
                    if (!pendingPacks.add(pack)) {
                        logger.warning("Ignoring Geyser pack " + pack.source + " as another pack with a higher version is present!");
                    }
                });
        } catch (IOException exception) {
            logger.error("Failed to list packs directory when reading Geyser packs!", exception);
            return List.of();
        }

        return pendingPacks.stream()
            .flatMap(pending -> pending.extract(cache, logger))
            .toList();
    }

    private static Stream<Pending> tryLoad(Path pack, GeyserLogger logger) {
        try (FileSystem system = FileSystems.newFileSystem(pack)) {
            Path manifestPath = system.getPath(MANIFEST_PATH);
            if (!Files.isRegularFile(manifestPath)) {
                throw new IOException("Missing " + MANIFEST_PATH);
            }
            if (Files.size(manifestPath) > MAX_MANIFEST_SIZE) {
                throw new IOException(MANIFEST_PATH + " is too large");
            }

            return Stream.of(new Pending(Manifest.tryLoad(manifestPath), pack));
        } catch (ProviderNotFoundException exception) {
            logger.error("Geyser pack " + pack + " is not a valid ZIP file!");
        } catch (IOException exception) {
            logger.error("Failed to load Geyser pack " + pack + ": ", exception);
        }
        return Stream.empty();
    }

    private static boolean isGeyserAssetPack(Path path) {
        return path.toString().endsWith(EXTENSION) && Files.isRegularFile(path);
    }

    /**
     * Rejects archive entries that could escape the extraction directory. Zip file systems normally normalize
     * {@code ..} segments, but a plain name check keeps this safe for every provider and platform.
     */
    static boolean isSafeRelativePath(Path relative) {
        if (relative.isAbsolute()) {
            return false;
        }
        for (Path segment : relative) {
            String name = segment.toString();
            if (name.isEmpty() || name.equals(".") || name.equals("..") || name.indexOf('\\') >= 0) {
                return false;
            }
        }
        return true;
    }

    private record Pending(Manifest manifest, Path source) {
        public static final Comparator<Pending> COMPARATOR = Comparator.<Pending>comparingInt(pending -> pending.manifest.version)
            .reversed()
            .thenComparing(Pending::source);

        public Stream<GeyserAssetPack> extract(Path cache, GeyserLogger logger) {
            try (FileSystem system = FileSystems.newFileSystem(source)) {
                Path packRoot = system.getPath("/");
                Path cacheRoot = cache.toRealPath();
                Path exportedRoot = cacheRoot.resolve(manifest.uuid.toString()).normalize();
                if (!exportedRoot.startsWith(cacheRoot)) {
                    logger.error("Refusing to extract Geyser pack " + source + " outside of the pack cache!");
                    return Stream.empty();
                }

                Path existingManifest = exportedRoot.resolve(MANIFEST_PATH);
                if (Files.isRegularFile(existingManifest)) {
                    boolean upToDate = false;
                    if (Files.size(existingManifest) <= MAX_MANIFEST_SIZE) {
                        try {
                            Manifest existing = Manifest.tryLoad(existingManifest);
                            if (existing.uuidAndVersionEqual(manifest)) {
                                logger.debug("Existing manifest for " + source + " is up-to-date, using it");
                                upToDate = true;
                            }
                        } catch (IOException exception) {
                            logger.debug("Failed parsing existing manifest for " + source + ", overwriting it: " + exception.getMessage());
                        }
                    }
                    if (upToDate) {
                        return Stream.of(new GeyserAssetPack(manifest, exportedRoot));
                    }
                    logger.debug("Existing manifest for " + source + " is outdated, overwriting it");

                    try (Stream<Path> existingFiles = Files.walk(exportedRoot)) {
                        existingFiles.sorted(Comparator.reverseOrder()).forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException exception) {
                                logger.error("Failed to delete existing extracted copy of " + source + ": ", exception);
                            }
                        });
                    }
                }

                Files.createDirectories(exportedRoot);

                List<Path> entries = new ArrayList<>();
                long totalSize = 0;
                try (Stream<Path> files = Files.walk(packRoot)) {
                    for (Path path : (Iterable<Path>) files::iterator) {
                        if (!Files.isRegularFile(path) || Files.isSymbolicLink(path)) {
                            continue;
                        }
                        Path relative = packRoot.relativize(path);
                        if (!isSafeRelativePath(relative) || !exportedRoot.resolve(relative.toString()).normalize().startsWith(exportedRoot)) {
                            logger.error("Geyser pack " + source + " contains an unsafe entry (" + relative + "), ignoring the pack!");
                            return Stream.empty();
                        }
                        long size = Files.size(path);
                        totalSize += size;
                        if (size > MAX_ENTRY_SIZE || totalSize > MAX_TOTAL_SIZE || entries.size() >= MAX_ENTRIES) {
                            logger.error("Geyser pack " + source + " exceeds the extraction limits, ignoring the pack!");
                            return Stream.empty();
                        }
                        entries.add(path);
                    }
                }

                for (Path path : entries) {
                    Path destination = exportedRoot.resolve(packRoot.relativize(path).toString()).normalize();
                    Files.createDirectories(destination.getParent());
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                }

                return Stream.of(new GeyserAssetPack(manifest, exportedRoot));
            } catch (IOException exception) {
                logger.error("Failed to extract " + source + "!", exception);
                return Stream.empty();
            }
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Pending otherPending && otherPending.manifest.uuid.equals(manifest.uuid);
        }

        @Override
        public int hashCode() {
            return manifest.uuid.hashCode();
        }
    }

    public record Manifest(UUID uuid, int version) {
        public boolean uuidAndVersionEqual(Manifest other) {
            return other.uuid.equals(uuid) && other.version == version;
        }

        public static Manifest tryLoad(Path manifest) throws IOException {
            JsonObject root = readMappingsRootSafely(manifest);
            try {
                UUID uuid = MappingsUtil.readOrThrow(root, "uuid", NodeReader.UUID, "reading pack manifest");
                int version = MappingsUtil.readOrThrow(root, "pack_version", NodeReader.NON_NEGATIVE_INT, "reading pack manifest");
                return new Manifest(uuid, version);
            } catch (InvalidCustomMappingsFileException exception) {
                throw new IOException("Malformed manifest: ", exception);
            }
        }

        private static JsonObject readMappingsRootSafely(Path file) throws IOException {
            JsonElement root;
            try {
                //noinspection deprecation
                root = new JsonParser().parse(Files.readString(file));
            } catch (JsonParseException exception) {
                throw new IOException("Malformed manifest: ", exception);
            }

            if (!root.isJsonObject()) {
                throw new IOException("Malformed manifest: is not a JSON object!");
            }

            JsonObject mappingsRoot = root.getAsJsonObject();
            JsonElement formatVersion = mappingsRoot.get("format_version");
            if (formatVersion == null || !formatVersion.isJsonPrimitive() || formatVersion.getAsInt() != 1) {
                throw new IOException("Manifest must specify \"format_version\" as a number, and it must be 1");
            }
            return mappingsRoot;
        }
    }
}
