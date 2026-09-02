package software.tnb.weaviate.resource.local;

import software.tnb.weaviate.service.Weaviate;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class WeaviateContainer extends GenericContainer<WeaviateContainer> {

    public WeaviateContainer(String image, int port, int grpcPort) {
        super(image);
        withExposedPorts(port, grpcPort);
        Weaviate.WEAVIATE_ENV.forEach(this::withEnv);
        waitingFor(Wait.forHttp("/v1/.well-known/ready").forPort(port));
    }
}
