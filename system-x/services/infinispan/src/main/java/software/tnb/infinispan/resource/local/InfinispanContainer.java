package software.tnb.infinispan.resource.local;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class InfinispanContainer extends GenericContainer<InfinispanContainer> {
    public InfinispanContainer(String image, int port, Map<String, String> env) {
        super(image);
        withExposedPorts(port);
        withEnv(env);
        waitingFor(Wait.forHttp("/console/"));
        withClasspathResourceMapping("/infinispan.xml", "/user-config/infinispan.xml", BindMode.READ_ONLY);
        setCommand("-c", "/user-config/infinispan.xml");
    }
}
