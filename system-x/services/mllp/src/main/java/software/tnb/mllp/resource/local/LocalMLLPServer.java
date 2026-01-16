package software.tnb.mllp.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.mllp.service.MLLPServer;

import com.google.auto.service.AutoService;

@AutoService(MLLPServer.class)
public class LocalMLLPServer extends MLLPServer implements ContainerDeployable<MLLPServerContainer> {
    private final MLLPServerContainer container = new MLLPServerContainer(image(), MLLPServer.LISTENING_PORT, containerEnvironment());

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(MLLPServer.LISTENING_PORT);
    }

    @Override
    public MLLPServerContainer container() {
        return container;
    }

    @Override
    public String getLogs() {
        return ContainerDeployable.super.getLogs();
    }
}
