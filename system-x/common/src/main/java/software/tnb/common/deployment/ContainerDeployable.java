package software.tnb.common.deployment;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.service.WithServiceDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.GenericContainer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public interface ContainerDeployable<T extends GenericContainer<?>> extends Deployable, WithServiceDefinition {
    Logger LOG = LoggerFactory.getLogger(ContainerDeployable.class);
    Map<ContainerDeployable<?>, String> VERSION_CACHE = new ConcurrentHashMap<>();

    default String containerServiceVersion() {
        return null;
    }

    default String resolveAndCacheServiceVersion() {
        String cached = VERSION_CACHE.get(this);
        if (cached != null) {
            return cached;
        }
        String version = WithServiceDefinition.super.serviceVersion();
        if (version == null || "latest".equals(version)) {
            String fromContainer = containerServiceVersion();
            if (fromContainer != null) {
                VERSION_CACHE.put(this, fromContainer);
                return fromContainer;
            }
        }
        return version;
    }

    @Override
    default String serviceVersion() {
        String cached = VERSION_CACHE.get(this);
        return cached != null ? cached : WithServiceDefinition.super.serviceVersion();
    }

    T container();

    default void deploy() {
        LOG.info("Starting {} container", serviceName());
        try {
            container().start();
        } catch (ContainerLaunchException e) {
            removeImage();
            throw e;
        }
        LOG.info("{} container started", serviceName());
        resolveAndCacheServiceVersion();
    }

    default void undeploy() {
        if (container().isRunning()) {
            LOG.info("Stopping {} container", serviceName());
            container().stop();
            LOG.info("{} container stopped", serviceName());

            removeImage();
        }
        VERSION_CACHE.remove(this);
    }

    default String getLogs() {
        return container().getLogs();
    }

    private void removeImage() {
        if (TestConfiguration.removeContainerImage()) {
            final DockerClient docker = DockerClientFactory.instance().client();
            // find the docker image inside Image#repoTags
            final Optional<Image> containerImage = docker.listImagesCmd().exec().stream()
                .filter(i -> Arrays.asList(i.getRepoTags()).contains(container().getDockerImageName())).findAny();
            if (containerImage.isEmpty()) {
                LOG.error("Unable to get the docker image for {} container - this should not happen", this.getClass().getSimpleName());
            } else {
                final Image image = containerImage.get();
                final long imageSize = image.getSize();
                final long threshold = TestConfiguration.removeContainerImageThresholdMB();
                if (imageSize > threshold) {
                    LOG.debug("Deleting image id {} (exceeded threshold: {} > {})", image.getId(), imageSize, threshold);
                    docker.removeImageCmd(image.getId()).withForce(true).exec();
                } else {
                    LOG.trace("Not removing image {} (below threshold: {} <= {}", image.getId(), imageSize, threshold);
                }
            }
        }
    }
}
