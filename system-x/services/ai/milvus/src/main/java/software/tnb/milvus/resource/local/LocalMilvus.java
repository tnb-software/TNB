package software.tnb.milvus.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.milvus.service.Milvus;

import com.google.auto.service.AutoService;

@AutoService(Milvus.class)
public class LocalMilvus extends Milvus implements ContainerDeployable<MilvusContainer> {

    private final MilvusContainer container = new MilvusContainer(defaultImage(), PORT);

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
    public MilvusContainer container() {
        return container;
    }
}
