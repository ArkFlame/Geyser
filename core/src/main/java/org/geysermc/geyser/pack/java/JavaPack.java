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

package org.geysermc.geyser.pack.java;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.pack.java.contents.JavaPackContents;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// com.google.common.hash
@SuppressWarnings("UnstableApiUsage")
public record JavaPack(Id id, JavaPackContents contents) {
    private static final HashFunction HASHER = Hashing.sha1();

    static Optional<JavaPack> open(Path path, Id id) {
        try {
            if (id.hash != null) {
                HashCode fileHash = HASHER.hashBytes(Files.readAllBytes(path));
                if (!fileHash.equals(id.hash)) {
                    GeyserImpl.getInstance().getLogger().error("Failed to load server resourcepack with UUID " + id.uuid + " because the hash did not match " +
                        "(ours: \"" + fileHash + "\", theirs: \"" + id.hash + "\")");
                    return Optional.empty();
                }
            }

            if (!hasSafeEntries(path)) {
                GeyserImpl.getInstance().getLogger().error("Failed to load server resourcepack with UUID " + id.uuid + " because it contains unsafe archive entries!");
                return Optional.empty();
            }

            try (FileSystem zip = FileSystems.newFileSystem(path)) {
                return Optional.of(new JavaPack(id, JavaPackContents.read(zip)));
            }
        } catch (IOException exception) {
            GeyserImpl.getInstance().getLogger().error("Failed to load server resourcepack with UUID " + id.uuid + "!", exception);
            return Optional.empty();
        }
    }

    /**
     * Checks all entries of a zip file for paths that could escape the archive root when extracted.
     *
     * @param path the zip file to inspect
     * @return true if every entry stays inside the archive root
     * @throws IOException if the archive could not be read
     */
    static boolean hasSafeEntries(Path path) throws IOException {
        try (ZipFile zip = new ZipFile(path.toFile())) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                if (!isSafeEntryName(entries.nextElement().getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Rejects absolute paths, Windows drive paths and any entry containing a {@code ..} segment.
     *
     * @param name the raw zip entry name
     * @return true if the entry is safe to extract inside the cache directory
     */
    static boolean isSafeEntryName(@Nullable String name) {
        if (name == null || name.isEmpty()) {
            return true;
        }
        String normalized = name.replace('\\', '/');
        if (normalized.startsWith("/")) {
            return false;
        }
        if (normalized.length() > 1 && normalized.charAt(1) == ':') {
            return false;
        }
        for (String part : normalized.split("/")) {
            if (part.equals("..")) {
                return false;
            }
        }
        return true;
    }

    public record Id(UUID uuid, @Nullable HashCode hash) {

        Path path(Path cacheDirectory) {
            return cacheDirectory.resolve(uuid.toString());
        }
    }
}
