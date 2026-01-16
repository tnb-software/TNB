package software.tnb.samba.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.samba.service.SambaServer;

import com.google.auto.service.AutoService;

@AutoService(SambaServer.class)
public class LocalSambaServer extends SambaServer implements ContainerDeployable<SambaServerContainer> {
    private final SambaServerContainer container = new SambaServerContainer(image(), containerEnvironment());

    @Override
    public int port() {
        return container.getMappedPort(SambaServer.SAMBA_PORT_DEFAULT);
    }

    @Override
    public String address() {
        return
            host() + ":" + port();
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public String shareName() {
        return "data-rw";
    }

    @Override
    public SambaServerContainer container() {
        return container;
    }
}
