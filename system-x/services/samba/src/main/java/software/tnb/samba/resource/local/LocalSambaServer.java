package software.tnb.samba.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.samba.service.SambaServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(SambaServer.class)
public class LocalSambaServer extends SambaServer implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSambaServer.class);

    private SambaServerContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Samba Server container");
        container = new SambaServerContainer(image(), containerEnvironment());
        container.start();
        LOG.info("Samba Server container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Samba Server container");
            container.stop();
        }
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public int port() {
        return container.getMappedPort(SambaServer.SAMBA_PORT_DEFAULT);
    }

    @Override
    public String address() {
        return
            container.getHost() + ":" + port();
    }

    @Override
    public String shareName() {
        return "data-rw";
    }

    @Override
    public String getLog() {
        return container.getLogs();
    }
}
