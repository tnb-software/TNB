package software.tnb.ftp.ftp.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class FTPContainer extends GenericContainer<FTPContainer> {

    public FTPContainer(String image, int port, Map<String, String> env) {
        super(image);
        withEnv(env);
        waitingFor(Wait.forLogMessage(".*FtpServer started.*", 1));
    }
}
