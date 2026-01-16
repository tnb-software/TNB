package software.tnb.ftp.sftp.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.utils.IOUtils;
import software.tnb.ftp.sftp.service.SFTP;

import com.google.auto.service.AutoService;

import java.io.IOException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

@AutoService(SFTP.class)
public class LocalSFTP extends SFTP implements ContainerDeployable<SFTPContainer> {
    private final SFTPContainer container = new SFTPContainer(image(), port(), containerEnvironment());

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

    @Override
    public SFTPContainer container() {
        return container;
    }
}
