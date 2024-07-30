package software.tnb.jms.amq.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.jms.amq.service.AMQBroker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(AMQBroker.class)
public class LocalAMQBroker extends AMQBroker implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalAMQBroker.class);
    private AMQBrokerContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting AMQ container");
        container = new AMQBrokerContainer(image(), containerEnvironment(), containerPorts());
        container.start();
        LOG.info("AMQ broker container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping AMQ broker container");
            container.stop();
        }
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int getPortMapping(int port) {
        return container.getMappedPort(port);
    }

    private int[] containerPorts() {
        return new int[] {8161, 61616, 1883, 5672};
    }

    @Override
    public String defaultImage() {
        return "registry.redhat.io/amq7/amq-broker-rhel8:7.12.1";
    }
}
