package software.tnb.ftp.ftp.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.List;
import java.util.Map;

public class FTPContainer extends GenericContainer<FTPContainer> {
    public FTPContainer(String image, Map<String, String> env, List<Integer> ports) {
        super(image);
        withEnv(env);
        ports.forEach(p -> addFixedExposedPort(p, p));
        waitingFor(Wait.forLogMessage(".*FtpServer started.*", 1));
    }
}
