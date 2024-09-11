package software.tnb.cxf.soap.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.cxf.soap.service.CxfSoap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.List;

@AutoService(CxfSoap.class)
public class LocalCxfSoap extends CxfSoap implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalCxfSoap.class);

    private CxfSoapContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting CXF SOAP container");
        container = new CxfSoapContainer(image(), List.of(PORT));
        container.start();
        LOG.info("CXF SOAP container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping CXF SOAP container");
            container.stop();
        }
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }
}
