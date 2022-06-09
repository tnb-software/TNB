package software.tnb.common.utils;

import software.tnb.common.config.TestConfiguration;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class IOUtils {
    private static final Logger LOG = LoggerFactory.getLogger(IOUtils.class);

    private IOUtils() {
    }

    public static void writeFile(Path file, String content) {
        try {
            Path dir = file.getParent();
            if (!Files.exists(dir)) {
                LOG.debug("Creating directory " + dir);
                Files.createDirectory(dir);
            }
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
     * @param f path to the directory or file
     * @return path to the tar file
     */
    public static Path createTar(Path f) {
        Path output;
        try {
            output = Files.createTempFile(TestConfiguration.appLocation(), "tar", ".tar");
        } catch (IOException e) {
            throw new RuntimeException("Unable to create temp file: ", e);
        }
        try (TarArchiveOutputStream archive = new TarArchiveOutputStream(Files.newOutputStream(output))) {
            archive.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            if (!f.toFile().isDirectory()) {
                addTarEntry(archive, f, f.getFileName().toString());
            } else {
                Files.walkFileTree(f, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                        if (attributes.isSymbolicLink()) {
                            return FileVisitResult.CONTINUE;
                        }
                        Path targetFile = f.relativize(file);
                        addTarEntry(archive, file, targetFile.toString());

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        return FileVisitResult.CONTINUE;
                    }
                });
            }

            archive.finish();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create tar file: ", e);
        }
        return output;
    }

    private static void addTarEntry(TarArchiveOutputStream taos, Path file, String fileName) {
        try {
            TarArchiveEntry tarEntry = new TarArchiveEntry(file.toFile(), fileName);
            taos.putArchiveEntry(tarEntry);
            Files.copy(file, taos);
            taos.closeArchiveEntry();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create tar entry: ", e);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        org.apache.commons.io.IOUtils.closeQuietly(closeable, e -> LOG.warn("Could not close resource", e));
    }

    public static void createDirectory(File f) {
        if (!f.exists()) {
            if (!f.mkdirs()) {
                LOG.debug("Creating new directory {}", f.getAbsolutePath());
                throw new RuntimeException("Unable to create directory " + f.getAbsolutePath());
            }
        }
    }

    public static void createDirectory(Path p) {
        createDirectory(p.toFile());
    }

    public static void copyDirectory(Path from, Path to) {
        try {
            LOG.debug("Copying directory {} to {}", from.toAbsolutePath(), to.toAbsolutePath());
            FileUtils.copyDirectory(from.toFile(), to.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Unable to copy directories: ", e);
        }
    }

    public static void replaceVariables(Path input, Properties keysValues, Path output) {
        String withVars = readFile(input);
        //can't use lambda because withVars would have to be effectively final
        for (Object key : keysValues.keySet()) {
            withVars = withVars.replaceAll(TestConfiguration.VARIABLE_PLACEHOLDER_START + key + TestConfiguration.VARIABLE_PLACEHOLDER_END,
                keysValues.getProperty(key.toString()));
        }
        writeFile(output, withVars);
    }

    public static Path zipFiles(String zipFileName, Path... files) {
        LOG.info("Creating zip file {}.zip from files: {}", zipFileName, Arrays.stream(files).map(f -> f.getFileName().toString())
            .collect(Collectors.toList()));
        final Path zipFile;
        try {
            zipFile = Files.createFile(Paths.get("/tmp", zipFileName + ".zip"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create temp zip file: ", e);
        }
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
            for (Path file : files) {
                ZipEntry e = new ZipEntry(file.getFileName().toString());
                zos.putNextEntry(e);
                zos.write(readFile(file).getBytes());
                zos.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to add file to zip file: ", e);
        }
        return zipFile;
    }

    public static String getExecInPath(String execName) {
        return Arrays.stream(System.getenv("PATH").split(File.pathSeparator)).map(Path::of)
            .filter(pathEntry -> {
                try {
                    return Files.find(pathEntry, 1, (filePath, basicFileAttributes) ->
                        Files.exists(filePath)
                            && !Files.isDirectory(filePath)
                            && filePath.getFileName().toString().equals(execName)).count() > 0;
                } catch (IOException e) {
                    //just skip
                    return false;
                }
            }).map(p -> p.resolve(execName).toAbsolutePath().toString()).findFirst().orElse(null);
    }
}
