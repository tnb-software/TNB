package software.tnb.filesystem.validation;

import software.tnb.common.utils.IOUtils;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class LocalFileSystemValidation extends FileSystemValidation {
    @Override
    public void createFile(Path file, String content) {
        createFile(file, content.getBytes());
    }

    @Override
    public void createFile(Path file, byte[] content) {
        IOUtils.writeFile(file, content);
    }

    @Override
    public void deleteFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Unable to delete file", e);
        }
    }

    @Override
    public byte[] getFile(Path file) {
        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file", e);
        }
    }

    @Override
    public void copyFile(Path srcPath, Path destPath) {
        try {
            Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Unable to copy file from " + srcPath + " to " + destPath, e);
        }
    }

    @Override
    public Path createDirectory(Path directory) {
        try {
            return Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create directory", e);
        }
    }

    @Override
    public void deleteDirectory(Path directory) {
        try {
            FileUtils.deleteDirectory(directory.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Unable to delete directory", e);
        }
    }

    @Override
    public boolean exists(Path path) {
        return path.toFile().exists();
    }
}
