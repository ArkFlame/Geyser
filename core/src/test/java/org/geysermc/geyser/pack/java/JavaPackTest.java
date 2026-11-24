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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaPackTest {

    @Test
    void acceptsNormalEntries() {
        assertTrue(JavaPack.isSafeEntryName("pack.mcmeta"));
        assertTrue(JavaPack.isSafeEntryName("assets/minecraft/lang/en_us.json"));
        assertTrue(JavaPack.isSafeEntryName("assets/minecraft/lang/..en_us.json"));
        assertTrue(JavaPack.isSafeEntryName(""));
    }

    @Test
    void rejectsPathTraversal() {
        assertFalse(JavaPack.isSafeEntryName("../evil.txt"));
        assertFalse(JavaPack.isSafeEntryName("assets/../../evil.txt"));
        assertFalse(JavaPack.isSafeEntryName("..\\..\\evil.txt"));
        assertFalse(JavaPack.isSafeEntryName("/etc/passwd"));
        assertFalse(JavaPack.isSafeEntryName("C:\\Windows\\system.ini"));
    }

    @Test
    void detectsTraversalEntryInZip(@TempDir Path tempDir) throws IOException {
        Path safe = writeZip(tempDir.resolve("safe.zip"), "assets/minecraft/lang/en_us.json");
        Path unsafe = writeZip(tempDir.resolve("unsafe.zip"), "../escape.txt");

        assertTrue(JavaPack.hasSafeEntries(safe));
        assertFalse(JavaPack.hasSafeEntries(unsafe));
    }

    private static Path writeZip(Path path, String entryName) throws IOException {
        try (OutputStream output = Files.newOutputStream(path);
             ZipOutputStream zip = new ZipOutputStream(output)) {
            zip.putNextEntry(new ZipEntry(entryName));
            zip.write("{}".getBytes());
            zip.closeEntry();
        }
        return path;
    }
}
