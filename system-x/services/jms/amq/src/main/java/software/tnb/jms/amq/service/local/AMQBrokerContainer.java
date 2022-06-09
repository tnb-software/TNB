package software.tnb.jms.amq.service.local;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class AMQBrokerContainer extends GenericContainer<AMQBrokerContainer> {

    public static final String IMAGE = System.getProperty("amq.image", "registry.redhat.io/amq7/amq-broker:latest");

    public AMQBrokerContainer(Map<String, String> env, int... exposedPorts) {
        super(IMAGE);
        this.addExposedPorts(exposedPorts);
        this.withEnv(env);
    }
}
