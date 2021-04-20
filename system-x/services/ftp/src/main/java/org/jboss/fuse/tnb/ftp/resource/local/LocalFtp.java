package org.jboss.fuse.tnb.ftp.resource.local;

import org.jboss.fuse.tnb.ftp.service.Ftp;
import org.jboss.fuse.tnb.common.config.SystemXConfiguration;
import org.jboss.fuse.tnb.common.deployment.Deployable;

import org.apache.commons.net.ftp.FTPClient;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import org.testcontainers.images.builder.Transferable;

import java.io.IOException;
import java.io.InputStream;

@AutoService(Ftp.class)
public class LocalFtp extends Ftp implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFtp.class);
    private FtpContainer container;
    private FTPClient client;

    @Override
    public void deploy() {
        LOG.info("Starting Ftp container");
        container = new FtpContainer(SystemXConfiguration.ftpImage(), port(), containerEnvironment());
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
    public FTPClient client() {
        if (client == null) {
            try {
                LOG.debug("Creating new FTPClient instance");
                client = new TestContainerFtpOutOfBandClient();
                client.connect(localClientHost(), port());
                client.login(account().username(), account().password());
                client.enterLocalPassiveMode();
                client.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                client.setDataTimeout(500000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return client;
    }

    @Override
    public int port() {
        return 2121;
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
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        undeploy();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        deploy();
    }

    /**
     * Custom client to make transfering files into the server faster and more stable
     */
    public class TestContainerFtpOutOfBandClient extends FTPClient {

        @Override
        public boolean storeFile(String fileName, InputStream fileContent) throws IOException {
            container.copyFileToContainer(Transferable.of(fileContent.readAllBytes(), 040777), "/tmp/" + fileName);
            return true;
        }
    }
}
