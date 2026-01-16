package software.tnb.cxf.soap.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class CXFSOAPContainer extends GenericContainer<CXFSOAPContainer> {

    public CXFSOAPContainer(String image, int port) {
        super(image);
        this.withExposedPorts(port);
        this.waitingFor(Wait.forLogMessage(".*Admin console listening on.*", 1));
    }
}
