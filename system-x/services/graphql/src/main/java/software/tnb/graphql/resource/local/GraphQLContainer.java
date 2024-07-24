package software.tnb.graphql.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.List;

public class GraphQLContainer extends GenericContainer<GraphQLContainer> {

    public GraphQLContainer(String image, List<Integer> ports) {
        super(image);
        this.withExposedPorts(ports.toArray(new Integer[0]));
        this.waitingFor(Wait.forLogMessage(".*serving empty GraphQL API.*", 1));
    }
}
