package software.tnb.milvus.resource.local;

import software.tnb.milvus.service.Milvus;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;

public class MilvusContainer extends GenericContainer<MilvusContainer> {

    public MilvusContainer(String image, int port) {
        super(image);
        this.withExposedPorts(port);
        Milvus.MILVUS_ENV.forEach(this::withEnv);
        this.withCopyToContainer(Transferable.of(Milvus.embedEtcdConfig()), "/milvus/configs/embedEtcd.yaml");
        this.withCommand("milvus", "run", "standalone");
        this.waitingFor(Wait.forLogMessage(".*Proxy successfully started.*", 1));
    }
}
