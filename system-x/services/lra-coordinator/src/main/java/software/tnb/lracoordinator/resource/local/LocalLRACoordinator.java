package software.tnb.lracoordinator.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.lracoordinator.service.LRACoordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(LRACoordinator.class)
public class LocalLRACoordinator extends LRACoordinator implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalLRACoordinator.class);
    private LRACoordinatorContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting LRA Coordinator container");
        container = new LRACoordinatorContainer(image(), containerEnvironment());
        container.start();
        LOG.info("LRA Coordinator container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            container.stop();
        }
    }

    @Override
    public String hostname() {
        return "localhost";
    }

    @Override
    public int port() {
        return container.getPort();
    }

    @Override
    public String getUrl() {
        return String.format("http://localhost:%s", port());
    }

    @Override
    public String getLog() {
        return container.getLogs();
    }
}
