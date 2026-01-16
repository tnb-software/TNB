package software.tnb.aws.common.resource.local;

import software.tnb.aws.common.service.LocalStack;
import software.tnb.common.deployment.ContainerDeployable;

import com.google.auto.service.AutoService;

@AutoService(LocalStack.class)
public class LocalLocalStack extends LocalStack implements ContainerDeployable<LocalStackContainer> {
    private final LocalStackContainer container = new LocalStackContainer(image(), PORT);

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public String serviceUrl() {
        return String.format("http://%s:%d", container.getHost(), container.getPort());
    }

    @Override
    public String clientUrl() {
        return serviceUrl();
    }

    @Override
    public LocalStackContainer container() {
        return container;
    }
}
