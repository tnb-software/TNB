package software.tnb.filesystem.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.utils.IOUtils;
import software.tnb.filesystem.service.FileSystem;

import com.google.auto.service.AutoService;

import java.nio.file.Path;

@AutoService(FileSystem.class)
public class LocalFileSystem extends FileSystem implements Deployable {
    @Override
    public void setAppName(String app) {
    }

    @Override
    public String getFileContent(Path path) {
        return IOUtils.readFile(path);
    }
}
