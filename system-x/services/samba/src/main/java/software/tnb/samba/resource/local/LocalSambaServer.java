package software.tnb.samba.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.samba.service.SambaServer;

import com.google.auto.service.AutoService;

@AutoService(SambaServer.class)
public class LocalSambaServer extends SambaServer implements ContainerDeployable<SambaServerContainer> {
    private final SambaServerContainer container = new SambaServerContainer(image(), containerEnvironment());

    @Override
    public String containerServiceVersion() {
        try {
            String v = container().execInContainer("sh", "-c",
                "smbd --version 2>/dev/null | sed 's|Version ||'")
                .getStdout().trim();
            return v.isEmpty() ? null : v;
        } catch (Exception e) {
            LOG.debug("Failed to detect Samba version from container", e);
            return null;
        }
    }

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
