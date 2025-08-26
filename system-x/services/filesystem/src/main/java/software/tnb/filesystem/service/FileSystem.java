package software.tnb.filesystem.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;

import java.io.IOException;
import java.nio.file.Path;

public abstract class FileSystem extends Service<NoAccount, NoClient, NoValidation> {
    public abstract void setAppName(String app);

    public abstract String getFileContent(Path path);

    public abstract boolean createFile(Path directory, String filename, String content) throws IOException;

    public abstract void copyFile(Path srcPath, Path destPath) throws IOException;

    public abstract Path createTempDirectory() throws IOException;

    public void deploy() {
    }

    public void undeploy() {
    }

    public void openResources() {
    }

    public void closeResources() {
    }
}
