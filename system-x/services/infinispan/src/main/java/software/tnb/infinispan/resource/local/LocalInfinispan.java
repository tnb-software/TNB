package software.tnb.infinispan.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.infinispan.service.Infinispan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Infinispan.class)
public class LocalInfinispan extends Infinispan implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalInfinispan.class);
    private InfinispanContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Infinispan container");
        container = new InfinispanContainer(defaultImage(), PORT, containerEnvironment());
        container.start();
        LOG.info("Infinispan container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            container.stop();
            LOG.info("Infinispan container stopped");
        }
    }

    @Override
    public int getPortMapping() {
        return container.getMappedPort(PORT);
    }

    @Override
    public String getHost() {
        return "localhost";
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }
}
