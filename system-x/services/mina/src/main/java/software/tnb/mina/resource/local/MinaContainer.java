package software.tnb.mina.resource.local;

import software.tnb.mina.service.Mina;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Path;

public class MinaContainer extends GenericContainer<MinaContainer> {

    private static final String AUTHORIZED_KEYS_PATH = "/home/test/.ssh/authorized_keys";

    public MinaContainer(String image, Path publicKeyPath) {
        super(image);
        withExposedPorts(Mina.SSHD_LISTENING_PORT);
        waitingFor(Wait.forLogMessage(".*Starting SSHD on port.*", 1));
        if (publicKeyPath != null) {
            withCopyFileToContainer(MountableFile.forHostPath(publicKeyPath), AUTHORIZED_KEYS_PATH);
        }
    }
}
