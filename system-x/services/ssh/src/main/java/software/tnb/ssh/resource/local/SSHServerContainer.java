package software.tnb.ssh.resource.local;

import software.tnb.ssh.service.SSHServer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class SSHServerContainer extends GenericContainer<SSHServerContainer> {

    public SSHServerContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        withExposedPorts(SSHServer.SSHD_LISTENING_PORT);
        waitingFor(Wait.forLogMessage(".*Server listening on.*", 1));
    }
}
