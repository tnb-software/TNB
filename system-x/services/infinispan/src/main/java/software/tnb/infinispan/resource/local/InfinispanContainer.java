package software.tnb.infinispan.resource.local;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.Map;

public class InfinispanContainer extends GenericContainer<InfinispanContainer> {
    public InfinispanContainer(String image, int port, Map<String, String> env) {
        super(image);
        if (TestcontainersConfiguration.getInstance().getEnvironment().get("DOCKER_HOST") != null) {
            // Use network mode "host" so that infinispan can bind itself to the public ip if a remote docker host is used
            withNetworkMode("host");
            // withExposedPorts doesn't work in combination with network mode "host"
            addFixedExposedPort(port, port);
        } else {
            addExposedPort(port);
        }
        withEnv(env);
        waitingFor(Wait.forHttp("/console/"));
        withClasspathResourceMapping("/infinispan.xml", "/user-config/infinispan.xml", BindMode.READ_ONLY);
        setCommand("-c", "/user-config/infinispan.xml");
    }
}
