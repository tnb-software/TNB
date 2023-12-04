package software.tnb.jms.ibm.mq.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.jms.ibm.mq.service.IBMMQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(IBMMQ.class)
public class LocalIBMMQ extends IBMMQ implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalIBMMQ.class);
    private IBMMQContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting IBM MQ container");
        container = new IBMMQContainer(image(), DEFAULT_PORT, containerEnvironment(), mqscConfig());
        container.start();
        LOG.info("IBM MQ container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            container.stop();
        }
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(DEFAULT_PORT);
    }
}
