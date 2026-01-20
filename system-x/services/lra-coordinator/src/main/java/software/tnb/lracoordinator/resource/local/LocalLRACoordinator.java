package software.tnb.lracoordinator.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.lracoordinator.service.LRACoordinator;

import com.google.auto.service.AutoService;

@AutoService(LRACoordinator.class)
public class LocalLRACoordinator extends LRACoordinator implements ContainerDeployable<LRACoordinatorContainer> {
    private final LRACoordinatorContainer container = new LRACoordinatorContainer(image(), containerEnvironment());

    @Override
    public String hostname() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getPort();
    }

    @Override
    public String getUrl() {
        return String.format("http://%s:%d", hostname(), port());
    }

    @Override
    public String getExternalUrl() {
        return getUrl();
    }

    @Override
    public LRACoordinatorContainer container() {
        return container;
    }

    @Override
    public String getLogs() {
        return ContainerDeployable.super.getLogs();
    }
}
