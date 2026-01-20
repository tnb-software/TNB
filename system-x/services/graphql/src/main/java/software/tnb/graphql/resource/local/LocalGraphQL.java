package software.tnb.graphql.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.graphql.service.GraphQL;

import com.google.auto.service.AutoService;

@AutoService(GraphQL.class)
public class LocalGraphQL extends GraphQL implements ContainerDeployable<GraphQLContainer> {
    private final GraphQLContainer container = new GraphQLContainer(image(), PORT);

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    @Override
    public GraphQLContainer container() {
        return container;
    }
}
