package software.tnb.ftp.sftp.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class SFTPContainer extends GenericContainer<SFTPContainer> {

    public SFTPContainer(String image, int port, Map<String, String> env) {
        super(image);
        withEnv(env);
        addFixedExposedPort(port, port);
        waitingFor(Wait.forLogMessage(".*Server listening on.*", 3));
    }
}
