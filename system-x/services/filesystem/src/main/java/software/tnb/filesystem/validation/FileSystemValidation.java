package software.tnb.filesystem.validation;

import software.tnb.common.validation.Validation;

import java.nio.file.Path;

public abstract class FileSystemValidation implements Validation {
    public abstract void createFile(Path file, String content);

    public abstract void createFile(Path file, byte[] content);

    public abstract void deleteFile(Path file);

    public abstract byte[] getFile(Path file);

    public abstract void copyFile(Path srcPath, Path destPath);

    public abstract Path createDirectory(Path directory);

    public abstract void deleteDirectory(Path directory);

    public abstract boolean exists(Path path);
}
