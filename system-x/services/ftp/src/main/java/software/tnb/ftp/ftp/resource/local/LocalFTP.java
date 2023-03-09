package software.tnb.ftp.ftp.resource.local;

import software.tnb.common.deployment.Deployable;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoService(FTP.class)
public class LocalFTP extends FTP implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFTP.class);
    private FTPContainer container;
    private final CustomFTPClient client = new TestContainerFTPOutOfBandClient();

    @Override
    public void deploy() {
        LOG.info("Starting Ftp container");
        container = new FTPContainer(image(), port(), containerEnvironment());
        container.start();
        LOG.info("Ftp container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Ftp container");
            container.stop();
        }
    }

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
    public int port() {
        return 2121;
    }

    @Override
    public String logs() {
        try {
            Map<String, String> env = Arrays.stream(container.execInContainer("env").getStdout().split("\n"))
                .collect(Collectors.toMap(
                    envPair -> envPair.split("=")[0],
                    envPair -> envPair.split("=")[1]));
            if (env.containsKey("FTP_SERVER_DIR")) {
                return container.execInContainer("cat", String.format("%s/res/log/ftpd.log", env.get("FTP_SERVER_DIR"))).getStdout();
            }
        } catch (IOException | InterruptedException e) {
            LOG.warn("unable to read log file", e);
        }
        return "";
    }

    @Override
    public String host() {
        // container.getIpAddress() always returns "localhost" (see https://github.com/testcontainers/testcontainers-java/issues/452 ),
        // we need the actual container ip address, because ftp requires multiple ports.
        // Also tried exposing all the ports and using localhost, but then the integration would not work for some reason
        //      (probably because the ftp server is listening for passive connections on the ip, not on localhost)
        return container.getContainerInfo().getNetworkSettings().getNetworks().get("bridge").getIpAddress();
    }

    @Override
    public String hostForActiveConnection() {
        return host();
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
