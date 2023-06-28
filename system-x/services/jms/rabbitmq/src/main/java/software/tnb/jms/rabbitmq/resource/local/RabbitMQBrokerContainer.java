package software.tnb.jms.rabbitmq.resource.local;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class RabbitMQBrokerContainer extends GenericContainer<RabbitMQBrokerContainer> {
    public RabbitMQBrokerContainer(String image, Map<String, String> env, int... exposedPorts) {
        super(image);
        this.addExposedPorts(exposedPorts);
        this.withEnv(env);
    }
}
