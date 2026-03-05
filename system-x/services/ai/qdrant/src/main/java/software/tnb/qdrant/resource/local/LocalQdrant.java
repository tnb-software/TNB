package software.tnb.qdrant.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.qdrant.service.Qdrant;

import com.google.auto.service.AutoService;

@AutoService(Qdrant.class)
public class LocalQdrant extends Qdrant implements ContainerDeployable<QdrantContainer> {

    private final QdrantContainer container = new QdrantContainer(defaultImage(), PORT);

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public QdrantContainer container() {
        return container;
    }
}
