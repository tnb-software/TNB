package software.tnb.opensearch.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.opensearch.service.Opensearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.google.auto.service.AutoService;

@AutoService(Opensearch.class)
public class LocalOpensearch extends Opensearch implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalOpensearch.class);

    private OpensearchContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Opensearch container");
        // Specify max 2GB of memory, seems to work ok, but without it the container can eat a lot of ram
        container = new OpensearchContainer(image(), PORT, containerEnvs())
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(1024L * 1024 * 1024 * 2));
        container.start();
        LOG.info("Opensearch container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Opensearch container");
            container.stop();
        }
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return TestcontainersConfiguration.getInstance().getEnvironment().get("DOCKER_HOST") == null
            ? container.getMappedPort(PORT) : PORT;
    }
}
