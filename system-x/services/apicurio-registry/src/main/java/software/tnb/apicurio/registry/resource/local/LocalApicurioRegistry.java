package software.tnb.apicurio.registry.resource.local;

import software.tnb.apicurio.registry.service.ApicurioRegistry;
import software.tnb.common.deployment.ContainerDeployable;

import com.google.auto.service.AutoService;

@AutoService(ApicurioRegistry.class)
public class LocalApicurioRegistry extends ApicurioRegistry implements ContainerDeployable<ApicurioRegistryContainer> {
    private final ApicurioRegistryContainer container = new ApicurioRegistryContainer(image());

    @Override
    public String url() {
        return String.format("http://%s:%d", container.getHost(), container.getPort());
    }

    @Override
    protected String clientUrl() {
        return url();
    }

    @Override
    public ApicurioRegistryContainer container() {
        return container;
    }
}
