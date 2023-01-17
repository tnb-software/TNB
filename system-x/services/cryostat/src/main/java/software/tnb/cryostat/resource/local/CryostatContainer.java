package software.tnb.cryostat.resource.local;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        try {
            CONF_PATH = createFolder("cryostat-conf", Stream.of("rules", "metadata"));
            PROBES_PATH = createFolder("cryostat-probes", null);
            RECORDINGS_PATH = createFolder("cryostat-recordings", Stream.of("file-uploads"));
            TEMPLATES_PATH = createFolder("cryostat-templates", null);
            CLIENTLIB_PATH = createFolder("cryostat-clientlib", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private static Path createFolder(String name, Stream<String> subFolders) throws IOException {
        final Path root = Files.createTempDirectory(name);
        allPermissionsAndDeleteOnExit(root);
        if (subFolders != null) {
            subFolders.forEach(folder -> {
                try {
                    allPermissionsAndDeleteOnExit(Files.createDirectories(Path.of(root.toAbsolutePath().toString(), folder)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return root;
    }

    private static void allPermissionsAndDeleteOnExit(Path directory) {
        final File file = directory.toFile();
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(true, false);
        file.deleteOnExit();
    }
}
