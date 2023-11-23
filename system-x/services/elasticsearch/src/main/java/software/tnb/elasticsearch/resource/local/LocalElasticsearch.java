package software.tnb.elasticsearch.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.elasticsearch.service.Elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.google.auto.service.AutoService;

@AutoService(Elasticsearch.class)
public class LocalElasticsearch extends Elasticsearch implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalElasticsearch.class);

    private ElasticsearchContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Elasticsearch container");
        // Specify max 2GB of memory, seems to work ok, but without it the container can eat a lot of ram
        container = new ElasticsearchContainer(image(), PORT, account().password())
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(1024L * 1024 * 1024 * 2));
        container.start();
        LOG.info("Elasticsearch container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Elasticsearch container");
            container.stop();
        }
    }

    @Override
    protected String clientHost() {
        return host();
    }

    @Override
    public String host() {
        return container.getHost() + ":" + (TestcontainersConfiguration.getInstance().getEnvironment().get("DOCKER_HOST") == null
            ? container.getMappedPort(PORT) : PORT);
    }

    public String defaultImage() {
        return "docker.elastic.co/elasticsearch/elasticsearch:" + version();
    }
}
