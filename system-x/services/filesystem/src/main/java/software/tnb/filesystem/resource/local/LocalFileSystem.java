package software.tnb.filesystem.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.filesystem.service.FileSystem;
import software.tnb.filesystem.validation.FileSystemValidation;
import software.tnb.filesystem.validation.LocalFileSystemValidation;

import com.google.auto.service.AutoService;

import java.nio.file.Path;
import java.nio.file.Paths;

@AutoService(FileSystem.class)
public class LocalFileSystem extends FileSystem implements Deployable {
    public FileSystemValidation validation() {
        if (validation == null) {
            validation = new LocalFileSystemValidation();
        }
        return validation;
    }

    @Override
    public Path root() {
        return Paths.get("target", "tnb-filesystem").toAbsolutePath();
    }
}
