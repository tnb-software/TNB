package software.tnb.mllp.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.mllp.service.MllpServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(MllpServer.class)
public class LocalMllpServer extends MllpServer implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalMllpServer.class);

    private MllpServerContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting MLLP Server container");
        container = new MllpServerContainer(image(), MllpServer.LISTENING_PORT, containerEnvironment());
        container.start();
        LOG.info("MLLP Server container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping MLLP Server container");
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
    public String host() {
        return "localhost";
    }

    @Override
    public int port() {
        return container.getMappedPort(MllpServer.LISTENING_PORT);
    }

    @Override
    public String getLog() {
        return container.getLogs();
    }
}
