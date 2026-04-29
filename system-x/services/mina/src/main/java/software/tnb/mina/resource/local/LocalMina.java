package software.tnb.mina.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.mina.service.Mina;

import com.google.auto.service.AutoService;

@AutoService(Mina.class)
public class LocalMina extends Mina implements ContainerDeployable<MinaContainer> {
    private MinaContainer container;

    @Override
    protected String clientHostname() {
        return host();
    }

    @Override
    protected int clientPort() {
        return port();
    }

    public void openResources() {
        super.openResources();
    }

    public void closeResources() {
        super.closeResources();
    }

    @Override
    public String host() {
        return this.container.getHost();
    }

    @Override
    public int port() {
        return this.container.getMappedPort(Mina.SSHD_LISTENING_PORT);
    }

    @Override
    public MinaContainer container() {
        return container;
    }

    @Override
    public void deploy() {
        container = new MinaContainer(image(), getConfiguration().publicKeyPath(), getConfiguration().caPublicKeyPath());
        ContainerDeployable.super.deploy();
    }
}
