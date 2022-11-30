package software.tnb.splunk.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

/**
 * 8000 port = UI
 */
public class SplunkContainer extends GenericContainer<SplunkContainer> {
    public SplunkContainer(String image, int containerApiPort, Map<String, String> env) {
        super(image);
        this.withEnv(env);
        this.withExposedPorts(containerApiPort, 8000);
        this.waitingFor(Wait.forLogMessage(".*Ansible playbook complete, will begin streaming.*", 1));
    }
}
