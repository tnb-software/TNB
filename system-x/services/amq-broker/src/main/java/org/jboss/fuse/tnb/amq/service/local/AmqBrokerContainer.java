package org.jboss.fuse.tnb.amq.service.local;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class AmqBrokerContainer extends GenericContainer<AmqBrokerContainer> {

    public static final String IMAGE = System.getProperty("amq.image", "registry.redhat.io/amq7/amq-broker:latest");

    public AmqBrokerContainer(Map<String, String> env, int... exposedPorts) {
        super(IMAGE);
        this.addExposedPorts(exposedPorts);
        this.withEnv(env);
    }
}
