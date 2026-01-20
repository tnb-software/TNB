package software.tnb.rest.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.rest.service.Rest;

import com.google.auto.service.AutoService;

@AutoService(Rest.class)
public class LocalRest extends Rest implements ContainerDeployable<RestContainer> {
    private final RestContainer container = new RestContainer(image(), PORT);

    @Override
    public RestContainer container() {
        return container;
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }
}
