package software.tnb.ssh.resource.local;

import software.tnb.ssh.service.SshServer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class SshServerContainer extends GenericContainer<SshServerContainer> {

    public SshServerContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        withExposedPorts(SshServer.SSHD_LISTENING_PORT);
        waitingFor(Wait.forLogMessage(".*Server listening on.*", 1));
    }
}
