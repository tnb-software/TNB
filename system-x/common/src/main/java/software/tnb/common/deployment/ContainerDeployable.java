package software.tnb.common.deployment;

import software.tnb.common.service.WithServiceDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.GenericContainer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Map;
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
        Path logFile = Paths.get("target", "container-deploy-" + serviceName() + new Date().getTime() + ".log");

        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardOpenOption.CREATE)) {
            container().withLogConsumer(frame -> {
                try {
                    writer.write(frame.getUtf8String());
                    writer.flush();
                } catch (IOException ignored) {
                }
            }).start();
        } catch (ContainerLaunchException e) {
            LOG.error("Container did not start successfully: ", e);
            try {
                LOG.error(Files.readString(logFile));
            } catch (IOException ex) {
                LOG.warn("Unable to read log file {}:", logFile, ex);
            }
            throw e;
        } catch (IOException ioe) {
            LOG.warn("Unable to create container log file", ioe);
        }

        LOG.info("{} container started", serviceName());
        resolveAndCacheServiceVersion();
    }

    default void undeploy() {
        if (container().isRunning()) {
            LOG.info("Stopping {} container", serviceName());
            container().stop();
            LOG.info("{} container stopped", serviceName());
        }
        VERSION_CACHE.remove(this);
    }

    default String getLogs() {
        return container().getLogs();
    }
}
