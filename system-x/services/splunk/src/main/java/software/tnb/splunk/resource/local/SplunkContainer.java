package software.tnb.splunk.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class SplunkContainer extends GenericContainer<SplunkContainer> {

    public SplunkContainer(String image, List<Integer> ports, Map<String, String> env) {
        super(image);
        this.withEnv(env);
        this.withExposedPorts(ports.toArray(new Integer[0]));
        this.waitingFor(Wait.forLogMessage(".*Ansible playbook complete, will begin streaming.*", 1));
        // slower on MacOS + colima docker
        this.withStartupTimeout(Duration.ofMinutes(5));
    }
}
