package software.tnb.aws.common.resource.local;

import software.tnb.aws.common.service.LocalStack;
import software.tnb.common.deployment.Deployable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(LocalStack.class)
public class LocalLocalStack extends LocalStack implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalLocalStack.class);
    private LocalStackContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting LocalStack container");
        container = new LocalStackContainer(image(), PORT);
        container.start();
        LOG.info("LocalStack container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping LocalStack container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public String serviceUrl() {
        return "http://localhost:" + container.getPort();
    }

    @Override
    public String clientUrl() {
        return serviceUrl();
    }
}
