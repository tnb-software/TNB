package software.tnb.fhir.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.fhir.service.FHIR;

import com.google.auto.service.AutoService;

@AutoService(FHIR.class)
public class LocalFHIR extends FHIR implements ContainerDeployable<FHIRContainer> {
    private FHIRContainer container;

    @Override
    public int getPortMapping() {
        return container.getMappedPort(PORT);
    }

    @Override
    public String getServerUrl() {
        return String.format("http://%s:%d/fhir/", container.getHost(), getPortMapping());
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public void deploy() {
        // the container environment is in the configuration, therefore we must delay creating container instance
        container = new FHIRContainer(image(), PORT, containerEnvironment());
        ContainerDeployable.super.deploy();
    }

    @Override
    public FHIRContainer container() {
        return container;
    }
}
