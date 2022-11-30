package software.tnb.jms.amq.resource.local;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class AMQBrokerContainer extends GenericContainer<AMQBrokerContainer> {
    public AMQBrokerContainer(String image, Map<String, String> env, int... exposedPorts) {
        super(image);
        this.addExposedPorts(exposedPorts);
        this.withEnv(env);
    }
}
