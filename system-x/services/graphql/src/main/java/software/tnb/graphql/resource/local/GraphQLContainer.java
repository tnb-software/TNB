package software.tnb.graphql.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class GraphQLContainer extends GenericContainer<GraphQLContainer> {
    public GraphQLContainer(String image, int port) {
        super(image);
        this.withExposedPorts(port);
        this.waitingFor(Wait.forLogMessage(".*serving empty GraphQL API.*", 1));
    }
}
