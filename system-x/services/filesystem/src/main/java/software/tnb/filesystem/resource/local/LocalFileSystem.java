package software.tnb.filesystem.resource.local;

import software.tnb.common.utils.IOUtils;
import software.tnb.filesystem.service.FileSystem;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import java.nio.file.Path;

@AutoService(FileSystem.class)
public class LocalFileSystem extends FileSystem {
    @Override
    public void setAppName(String app) {
    }

    @Override
    public String getFileContent(Path path) {
        return IOUtils.readFile(path);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
    }
}
