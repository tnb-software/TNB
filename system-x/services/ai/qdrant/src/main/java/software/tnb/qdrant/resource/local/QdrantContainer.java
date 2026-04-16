package software.tnb.qdrant.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class QdrantContainer extends GenericContainer<QdrantContainer> {

    public QdrantContainer(String image, int port, int grpcPort) {
        super(image);
        this.withExposedPorts(port, grpcPort);
        this.waitingFor(Wait.forLogMessage(".*starting in Actix runtime.*", 1));
    }
}
