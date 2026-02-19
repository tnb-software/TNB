package software.tnb.docling.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class DoclingServeContainer extends GenericContainer<DoclingServeContainer> {

    public DoclingServeContainer(String image, int port) {
        super(image);
        this.withExposedPorts(port);
        this.waitingFor(Wait.forHttp("/health").forPort(port).forStatusCode(200));
    }
}
