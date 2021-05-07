package org.jboss.fuse.tnb.common.utils;

import org.jboss.fuse.tnb.common.config.TestConfiguration;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class IOUtils {
    private static final Logger LOG = LoggerFactory.getLogger(IOUtils.class);

    private IOUtils() {
    }

    public static void writeFile(Path file, String content) {
        try {
            Files.write(file, content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unable to write to " + file, e);
        }
    }

    public static String readFile(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file " + file, e);
        }
    }

    /**
     * Creates a new tar file from given directory.
     *
     * @param dir path to the directory
     * @return path to the tar file
     */
    public static Path createTar(Path dir) {
        Path output;
        try {
            output = Files.createTempFile(TestConfiguration.appLocation(), "tar", "");
        } catch (IOException e) {
            throw new RuntimeException("Unable to create temp file: ", e);
        }
        try (TarArchiveOutputStream archive = new TarArchiveOutputStream(Files.newOutputStream(output))) {
            archive.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    if (attributes.isSymbolicLink()) {
                        return FileVisitResult.CONTINUE;
                    }
                    Path targetFile = dir.relativize(file);
                    try {
                        TarArchiveEntry tarEntry = new TarArchiveEntry(file.toFile(), targetFile.toString());
                        archive.putArchiveEntry(tarEntry);
                        Files.copy(file, archive);
                        archive.closeArchiveEntry();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });

            archive.finish();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create tar file: ", e);
        }
        return output;
    }

    public static void closeQuietly(Closeable closeable) {
        org.apache.commons.io.IOUtils.closeQuietly(closeable, e -> LOG.warn("Could not close resource", e));
    }
}
