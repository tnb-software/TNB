package software.tnb.elasticsearch.resource.local;

import software.tnb.common.account.Accounts;
import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.elasticsearch.account.ElasticsearchAccount;
import software.tnb.elasticsearch.service.Elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import com.google.auto.service.AutoService;

@AutoService(Elasticsearch.class)
public class LocalElasticsearch extends Elasticsearch implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalElasticsearch.class);
    private static final String PASSWORD = "password";

    private ElasticsearchContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Elasticsearch container");
        // Specify max 1GB of memory, seems to work ok, but without it the container can eat a lot of ram
        container = new ElasticsearchContainer(image()).withPassword(PASSWORD)
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(1024L * 1024 * 1024));
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
    public ElasticsearchAccount account() {
        if (account == null) {
            account = Accounts.get(ElasticsearchAccount.class);
            account.setPassword(PASSWORD);
        }
        return account;
    }

    @Override
    protected String clientHost() {
        return container.getHttpHostAddress();
    }

    @Override
    public String host() {
        return container.getHttpHostAddress();
    }

    public String defaultImage() {
        return "docker.elastic.co/elasticsearch/elasticsearch:" + version();
    }
}
