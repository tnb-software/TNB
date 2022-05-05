package org.jboss.fuse.tnb.hyperfoil.service.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class HyperfoilContainer extends GenericContainer<HyperfoilContainer> {

    public static final String IMAGE = System.getProperty("hyperfoil.image", "quay.io/hyperfoil/hyperfoil:latest");

    public HyperfoilContainer(Map<String, String> env, int... exposedPorts) {
        super(IMAGE);
        this.withEnv(env);
        this.withNetworkMode("host");
        this.waitingFor(Wait.forHttp("/benchmark"));
        this.withCommand("standalone");
    }
}
