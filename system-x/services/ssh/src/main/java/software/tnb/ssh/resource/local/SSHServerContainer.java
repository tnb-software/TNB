package software.tnb.ssh.resource.local;

import software.tnb.ssh.service.SSHServer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Path;
import java.util.Map;

public class SSHServerContainer extends GenericContainer<SSHServerContainer> {

    public SSHServerContainer(String image, Map<String, String> env, Path publicKeyPath) {
        super(image);
        withEnv(env);
        withExposedPorts(SSHServer.SSHD_LISTENING_PORT);

        // Mount public key if provided
        if (publicKeyPath != null) {
            withCopyFileToContainer(MountableFile.forHostPath(publicKeyPath), "/etc/ssh/tnb_authorized_key");
        }

        waitingFor(Wait.forLogMessage(".*Server listening on.*", 1));
    }
}
