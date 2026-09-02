package software.tnb.weaviate.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.weaviate.service.Weaviate;

import com.google.auto.service.AutoService;

@AutoService(Weaviate.class)
public class LocalWeaviate extends Weaviate implements ContainerDeployable<WeaviateContainer> {

    private final WeaviateContainer container = new WeaviateContainer(image(), PORT, GRPC_PORT);

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    @Override
    public int grpcPort() {
        return container.getMappedPort(GRPC_PORT);
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public WeaviateContainer container() {
        return container;
    }
}
