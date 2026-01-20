package software.tnb.infinispan.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.infinispan.service.Infinispan;

import org.testcontainers.utility.TestcontainersConfiguration;

import com.google.auto.service.AutoService;

@AutoService(Infinispan.class)
public class LocalInfinispan extends Infinispan implements ContainerDeployable<InfinispanContainer>, WithDockerImage {
    private final InfinispanContainer container = new InfinispanContainer(image(), PORT, containerEnvironment());

    @Override
    public int getPortMapping() {
        return TestcontainersConfiguration.getInstance().getEnvironment().get("DOCKER_HOST") == null ? container.getMappedPort(PORT) : PORT;
    }

    @Override
    public String getHost() {
        return container.getHost();
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public InfinispanContainer container() {
        return container;
    }
}
