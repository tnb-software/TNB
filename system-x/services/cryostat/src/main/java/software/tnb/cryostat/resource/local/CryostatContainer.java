package software.tnb.cryostat.resource.local;

import software.tnb.common.utils.IOUtils;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public class CryostatContainer extends GenericContainer<CryostatContainer> {
    private static final Path CONF_PATH;
    private static final Path PROBES_PATH;
    private static final Path RECORDINGS_PATH;
    private static final Path TEMPLATES_PATH;
    private static final Path CLIENTLIB_PATH;

    static {
        CONF_PATH = IOUtils.createTempFolderStructure("cryostat-conf", Stream.of("rules", "metadata"));
        PROBES_PATH = IOUtils.createTempFolderStructure("cryostat-probes");
        RECORDINGS_PATH = IOUtils.createTempFolderStructure("cryostat-recordings", Stream.of("file-uploads"));
        TEMPLATES_PATH = IOUtils.createTempFolderStructure("cryostat-templates");
        CLIENTLIB_PATH = IOUtils.createTempFolderStructure("cryostat-clientlib");
    }

    public CryostatContainer(String image, Map<String, String> env) {
        super(image);
        this.withEnv(env);
        this.withFileSystemBind(CONF_PATH.toAbsolutePath().toString(), "/opt/cryostat.d/conf.d", BindMode.READ_WRITE);
        this.withFileSystemBind(PROBES_PATH.toAbsolutePath().toString(), "/opt/cryostat.d/probes.d", BindMode.READ_WRITE);
        this.withFileSystemBind(RECORDINGS_PATH.toAbsolutePath().toString(), "/opt/cryostat.d/recordings.d", BindMode.READ_WRITE);
        this.withFileSystemBind(TEMPLATES_PATH.toAbsolutePath().toString(), "/opt/cryostat.d/templates.d", BindMode.READ_WRITE);
        this.withFileSystemBind(CLIENTLIB_PATH.toAbsolutePath().toString(), "/opt/cryostat.d/clientlib.d", BindMode.READ_WRITE);
        this.withNetworkMode("host"); // using host network
    }
}
