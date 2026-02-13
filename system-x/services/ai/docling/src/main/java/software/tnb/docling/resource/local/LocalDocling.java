package software.tnb.docling.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.docling.service.Docling;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Docling.class)
public class LocalDocling extends Docling implements ContainerDeployable<DoclingContainer> {
    private static final Logger LOG = LoggerFactory.getLogger(LocalDocling.class);

    private final DoclingContainer container = new DoclingContainer(defaultImage(), PORT);

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
                LOG.warn("Unable to close Docling client", e);
            }
        }
    }

    @Override
    public DoclingContainer container() {
        return container;
    }
}
