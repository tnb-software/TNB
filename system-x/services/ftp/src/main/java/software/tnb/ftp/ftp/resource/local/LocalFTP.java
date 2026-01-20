package software.tnb.ftp.ftp.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.ftp.ftp.service.CustomFTPClient;
import software.tnb.ftp.ftp.service.FTP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.images.builder.Transferable;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@AutoService(FTP.class)
public class LocalFTP extends FTP implements ContainerDeployable<FTPContainer> {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFTP.class);
    private final FTPContainer container = new FTPContainer(image(), containerEnvironment(), containerPorts());
    private final CustomFTPClient client = new TestContainerFTPOutOfBandClient();

    @Override
    public void openResources() {
        // noop
    }

    @Override
    public void closeResources() {
        // noop
    }

    @Override
    protected CustomFTPClient client() {
        return client;
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public String hostForActiveConnection() {
        return host();
    }

    @Override
    public FTPContainer container() {
        return container;
    }

    /**
     * Custom client to make transfering files into the server faster and more stable.
     */
    public class TestContainerFTPOutOfBandClient implements CustomFTPClient {

        @Override
        public void storeFile(String fileName, InputStream fileContent) throws IOException {
            container.copyFileToContainer(Transferable.of(fileContent.readAllBytes(), 040777),
                basePath() + "/" + fileName);
        }

        @Override
        public void retrieveFile(String fileName, OutputStream local) throws IOException {
            Path tempFile = Files.createTempFile(null, null);
            try {
                container.copyFileFromContainer(basePath() + "/" + fileName, tempFile.toString());
                org.apache.commons.io.IOUtils.copy(Files.newInputStream(tempFile), local);
            } finally {
                tempFile.toFile().delete();
            }
        }

        @Override
        public void makeDirectory(String dirName) throws IOException {
            try {
                container.execInContainer("mkdir", "-m", "a=rwx", String.format("%s/%s", basePath(), dirName));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public List<String> listFolder(String dirName) throws IOException {
            try {
                return List.of(container.execInContainer("/bin/bash", "-c", String.format("ls -p %s/%s | grep -v /", basePath(), dirName))
                    .getStdout().split("\n"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
