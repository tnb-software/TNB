package software.tnb.docling.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.docling.service.DoclingServe;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(DoclingServe.class)
public class LocalDoclingServe extends DoclingServe implements ContainerDeployable<DoclingServeContainer> {
    private static final Logger LOG = LoggerFactory.getLogger(LocalDoclingServe.class);

    private final DoclingServeContainer container = new DoclingServeContainer(defaultImage(), PORT);

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
        client = HttpClients.createDefault();
    }

    @Override
    public void closeResources() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                LOG.warn("Unable to close DoclingServe client", e);
            }
        }
    }

    @Override
    public DoclingServeContainer container() {
        return container;
    }
}
