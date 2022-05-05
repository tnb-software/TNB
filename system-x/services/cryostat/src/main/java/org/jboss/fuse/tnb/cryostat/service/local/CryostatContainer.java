package org.jboss.fuse.tnb.cryostat.service.local;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class CryostatContainer extends GenericContainer<CryostatContainer> {

    public static final String IMAGE = System.getProperty("cryostat.image", "registry.redhat.io/cryostat-20-tech-preview/cryostat-rhel8:latest");

    public CryostatContainer(Map<String, String> env, int... exposedPorts) {
        super(IMAGE);
        env.put("CRYOSTAT_CONFIG_PATH", "/tmp");
        this.withEnv(env);
        this.withNetworkMode("host"); // using host network
    }
}
