package software.tnb.apicurio.registry.resource.local;

import software.tnb.apicurio.registry.service.ApicurioRegistry;
import software.tnb.common.deployment.Deployable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(ApicurioRegistry.class)
public class LocalApicurioRegistry extends ApicurioRegistry implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalApicurioRegistry.class);
    private ApicurioRegistryContainer container;

    @Override
    public String url() {
        return String.format("http://%s:%d", container.getHost(), container.getPort());
    }

    @Override
    protected String clientUrl() {
        return url();
    }

    @Override
    public void deploy() {
        LOG.info("Starting Apicurio Registry container");
        container = new ApicurioRegistryContainer(image());
        container.start();
        LOG.info("Apicurio Registry container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Apicurio Registry container");
            container.stop();
        }
    }

}
