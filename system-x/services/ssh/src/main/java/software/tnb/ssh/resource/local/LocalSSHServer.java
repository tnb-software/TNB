package software.tnb.ssh.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.ssh.service.SSHServer;

import com.google.auto.service.AutoService;

@AutoService(SSHServer.class)
public class LocalSSHServer extends SSHServer implements ContainerDeployable<SSHServerContainer> {
    private final SSHServerContainer container = new SSHServerContainer(image(), containerEnvironment());

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public String host() {
        return this.container.getHost();
    }

    @Override
    public int port() {
        return this.container.getMappedPort(SSHServer.SSHD_LISTENING_PORT);
    }

    @Override
    public SSHServerContainer container() {
        return container;
    }
}
