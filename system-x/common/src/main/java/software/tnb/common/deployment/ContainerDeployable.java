package software.tnb.common.deployment;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

public interface ContainerDeployable<T extends GenericContainer<?>> extends Deployable {
    Logger LOG = LoggerFactory.getLogger(ContainerDeployable.class);

    T container();

    default void deploy() {
        LOG.info("Starting {} container", serviceName());
        container().start();
        LOG.info("{} container started", serviceName());
    }

    default void undeploy() {
        if (container().isRunning()) {
            LOG.info("Stopping {} container", serviceName());
            container().stop();
            LOG.info("{} container stopped", serviceName());
        }
    }

    default String getLogs() {
        return container().getLogs();
    }

    private String serviceName() {
        return StringUtils.substringAfter(this.getClass().getSimpleName(), "Local");
    }
}
