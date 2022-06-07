package org.jboss.fuse.tnb.cryostat.service.local;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class CryostatContainer extends GenericContainer<CryostatContainer> {

    public static final String IMAGE = System.getProperty("cryostat.image", "registry.redhat.io/cryostat-tech-preview/cryostat-rhel8:latest");

    private static final String TMP_FOLDER = System.getProperty("java.io.tmpdir");

    public CryostatContainer(Map<String, String> env, int... exposedPorts) {
        super(IMAGE);
        env.put("CRYOSTAT_CONFIG_PATH", TMP_FOLDER);
        env.put("CRYOSTAT_PROBE_TEMPLATE_PATH", TMP_FOLDER);
        env.put("CRYOSTAT_ARCHIVE_PATH", TMP_FOLDER);
        this.withEnv(env);
        this.withNetworkMode("host"); // using host network
    }
}
