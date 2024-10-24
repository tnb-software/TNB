package software.tnb.product.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    private ZipUtils() {
    }

    public static void unzip(Path source, Path target) throws IOException {
        Set<PosixFilePermission> executePermissions = new HashSet<>();
        executePermissions.add(PosixFilePermission.OTHERS_EXECUTE);
        executePermissions.add(PosixFilePermission.OTHERS_WRITE);
        executePermissions.add(PosixFilePermission.OTHERS_READ);
        executePermissions.add(PosixFilePermission.GROUP_EXECUTE);
        executePermissions.add(PosixFilePermission.GROUP_WRITE);
        executePermissions.add(PosixFilePermission.GROUP_READ);
        executePermissions.add(PosixFilePermission.OWNER_EXECUTE);
        executePermissions.add(PosixFilePermission.OWNER_WRITE);
        executePermissions.add(PosixFilePermission.OWNER_READ);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = zipEntry.getName().endsWith(File.separator);

                Path newPath = zipSlipProtect(zipEntry, target);

                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }

                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                    if (newPath.getFileName().toString().endsWith(".sh")) {
                        Files.setPosixFilePermissions(newPath, executePermissions);
                    }
                }

                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();

        }

    }

    private static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
        throws IOException {
        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }
}
