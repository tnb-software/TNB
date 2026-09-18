package software.tnb.cyberark.cyberarkvault.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Map;

public class CyberarkVaultContainer extends GenericContainer<CyberarkVaultContainer> {

    public CyberarkVaultContainer(String image, Map<String, String> env) {
        super(image);
        // use networkMode host so that it can reach the pg container on localhost
        withNetworkMode("host");
        withEnv(env);
        withCommand("server");
        waitingFor(Wait.forLogMessage(".*Listening on.*", 1).withStartupTimeout(Duration.ofMinutes(5)));
    }
}
