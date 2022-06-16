package software.tnb.cryostat.resource.local;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class CryostatContainer extends GenericContainer<CryostatContainer> {
    private static final String TMP_FOLDER = System.getProperty("java.io.tmpdir");

    public CryostatContainer(String image, Map<String, String> env) {
        super(image);
        env.put("CRYOSTAT_CONFIG_PATH", TMP_FOLDER);
        env.put("CRYOSTAT_PROBE_TEMPLATE_PATH", TMP_FOLDER);
        env.put("CRYOSTAT_ARCHIVE_PATH", TMP_FOLDER);
        this.withEnv(env);
        this.withNetworkMode("host"); // using host network
    }
}
