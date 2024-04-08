package software.tnb.ftp.sftp.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.utils.IOUtils;
import software.tnb.ftp.sftp.service.SFTP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

@AutoService(SFTP.class)
public class LocalSFTP extends SFTP implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalSFTP.class);
    private SFTPContainer container;

    @Override

    public void deploy() {
        LOG.info("Starting SFTP container");
        container = new SFTPContainer(image(), port(), containerEnvironment());
        container.start();
        LOG.info("SFTP container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping SFTP container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
        executor.shutdownNow();
        IOUtils.closeQuietly(client);
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
    public String logs() {
        return container.getLogs();
    }

    @Override
    protected SFTPClient makeClient() {
        try {
            SSHClient sshClient = new SSHClient();
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(host(), port());
            sshClient.authPassword(account().username(), account().password());
            return sshClient.newSFTPClient();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create new SFTPClient instance", e);
        }
    }
}
