package software.tnb.milvus.resource.local;

import software.tnb.common.utils.IOUtils;
import software.tnb.milvus.service.Milvus;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.nio.file.Path;

public class MilvusContainer extends GenericContainer<MilvusContainer> {

    public MilvusContainer(String image, int port) {
        super(image);
        this.withExposedPorts(port);
        Milvus.MILVUS_ENV.forEach(this::withEnv);
        this.withFileSystemBind(createEtcdConfig(), "/milvus/configs/embedEtcd.yaml", BindMode.READ_ONLY);
        this.withCommand("milvus", "run", "standalone");
        this.waitingFor(Wait.forLogMessage(".*Proxy successfully started.*", 1));
    }

    private static String createEtcdConfig() {
        Path configFile = IOUtils.createTempFolderStructure("milvus").resolve("embedEtcd.yaml");
        IOUtils.writeFile(configFile, Milvus.embedEtcdConfig());
        return configFile.toAbsolutePath().toString();
    }
}
