package software.tnb.cxf.soap.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.List;

public class CxfSoapContainer extends GenericContainer<CxfSoapContainer> {

    public CxfSoapContainer(String image, List<Integer> ports) {
        super(image);
        this.withExposedPorts(ports.toArray(new Integer[0]));
        this.waitingFor(Wait.forLogMessage(".*Admin console listening on.*", 1));
    }
}
