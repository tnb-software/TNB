package software.tnb.searchengine.common.resource.local;

import software.tnb.common.deployment.Deployable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalSearch implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalSearch.class);

    private final SearchContainer container;

    public LocalSearch(SearchContainer container) {
        // Specify max 2GB of memory, seems to work ok, but without it the container can eat a lot of ram
        this.container = container
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(1024L * 1024 * 1024 * 2));
    }

    @Override
    public void deploy() {
        LOG.info("Starting container");
        container.start();
        LOG.info("Container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping the container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
        // no-op
    }

    @Override
    public void closeResources() {
        // no-op
    }
}
