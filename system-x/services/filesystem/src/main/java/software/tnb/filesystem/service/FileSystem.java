package software.tnb.filesystem.service;

import software.tnb.common.service.Service;

import java.nio.file.Path;

public abstract class FileSystem implements Service {
    public abstract void setAppName(String app);

    public abstract String getFileContent(Path path);
}
