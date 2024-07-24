package software.tnb.graphql.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.graphql.service.GraphQL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.List;

@AutoService(GraphQL.class)
public class LocalGraphQL extends GraphQL implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalGraphQL.class);

    private GraphQLContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting GraphQL container");
        List<Integer> ports = new ArrayList<>();
        ports.add(PORT);
        container = new GraphQLContainer(image(), ports);
        container.start();
        LOG.info("GraphQL container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping GraphQL container");
            container.stop();
        }
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }
}
