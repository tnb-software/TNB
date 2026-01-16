package software.tnb.cxf.soap.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.cxf.soap.service.CXFSOAP;

import com.google.auto.service.AutoService;

@AutoService(CXFSOAP.class)
public class LocalCXFSOAP extends CXFSOAP implements ContainerDeployable<CXFSOAPContainer> {
    private final CXFSOAPContainer container = new CXFSOAPContainer(image(), PORT);

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    @Override
    public CXFSOAPContainer container() {
        return container;
    }
}
