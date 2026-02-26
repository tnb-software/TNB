package software.tnb.jms.amq.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.jms.amq.service.AMQBroker;

import com.google.auto.service.AutoService;

@AutoService(AMQBroker.class)
public class LocalAMQBroker extends AMQBroker implements ContainerDeployable<AMQBrokerContainer>, WithDockerImage {
    private AMQBrokerContainer container;

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
        return "registry.redhat.io/amq7/amq-broker-rhel9:7.13.3";
    }

    @Override
    public void deploy() {
        // the container environment is in the configuration, therefore we must delay creating container instance
        container = new AMQBrokerContainer(image(), containerEnvironment(), containerPorts());
        ContainerDeployable.super.deploy();
    }

    @Override
    public AMQBrokerContainer container() {
        return container;
    }
}
