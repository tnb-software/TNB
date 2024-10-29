package software.tnb.jms.ibm.mq.resource.local;

import software.tnb.common.utils.IOUtils;
import software.tnb.jms.ibm.mq.service.IBMMQ;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class IBMMQContainer extends GenericContainer<IBMMQContainer> {
    private static final Path mqscCommandFilePath = Paths.get("target/" + IBMMQ.MQSC_COMMAND_FILE_NAME);
    private static final Path keysFolderPath = Paths.get("target/keys");

    public IBMMQContainer(String image, int port, Map<String, String> env, String mqsc, Path privateKey, Path publicKey) {
        super(image);
        createLocalMqscCommandFile(mqsc);
        withExposedPorts(port);
        withCopyFileToContainer(MountableFile.forHostPath(mqscCommandFilePath.toAbsolutePath()),
            IBMMQ.MQSC_COMMAND_FILES_LOCATION + "/" + IBMMQ.MQSC_COMMAND_FILE_NAME);

        if (Objects.nonNull(privateKey) && Objects.nonNull(publicKey)) {
            try {
                Files.createDirectories(keysFolderPath);
                final Path keyFile = Paths.get(keysFolderPath.toAbsolutePath().toString(), "key.key");
                Files.copy(privateKey, keyFile, StandardCopyOption.REPLACE_EXISTING);
                final Path certFile = Paths.get(keysFolderPath.toAbsolutePath().toString(), "key.crt");
                Files.copy(publicKey, certFile, StandardCopyOption.REPLACE_EXISTING);
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-r--r--");
                Files.setPosixFilePermissions(keyFile, perms);
                Files.setPosixFilePermissions(certFile, perms);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            withCopyFileToContainer(MountableFile.forHostPath(keysFolderPath.toAbsolutePath()), IBMMQ.KEYS_FOLDER);
        }

        withEnv(env);
        // AMQ5806I is a message code for queue manager start
        waitingFor(Wait.forLogMessage(".*AMQ5806I.*", 1));
    }

    private void createLocalMqscCommandFile(String content) {
        IOUtils.writeFile(mqscCommandFilePath, content);
    }
}
