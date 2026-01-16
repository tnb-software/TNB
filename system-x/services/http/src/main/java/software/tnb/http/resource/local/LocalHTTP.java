package software.tnb.http.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.http.service.HTTP;

import com.google.auto.service.AutoService;

@AutoService(HTTP.class)
public class LocalHTTP extends HTTP implements ContainerDeployable<HTTPContainer> {
    private final HTTPContainer container = new HTTPContainer(image());

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public String getHost() {
        return container.getHost();
    }

    @Override
    public int getHttpPort() {
        return container.getHttpPort();
    }

    @Override
    public int getHttpsPort() {
        return container.getHttpsPort();
    }

    @Override
    public String getLogs() {
        return ContainerDeployable.super.getLogs();
    }

    @Override
    public String httpUrl() {
        return String.format("http://%s:%d/", container.getHost(), container.getHttpPort());
    }

    @Override
    public String httpsUrl() {
        return String.format("https://%s:%d/", container.getHost(), container.getHttpsPort());
    }

    @Override
    public HTTPContainer container() {
        return container;
    }
}
