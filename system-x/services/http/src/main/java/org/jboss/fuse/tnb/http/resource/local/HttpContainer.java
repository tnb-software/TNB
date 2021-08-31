package org.jboss.fuse.tnb.http.resource.local;

import static org.jboss.fuse.tnb.http.service.HttpService.HTTPS_PORT;
import static org.jboss.fuse.tnb.http.service.HttpService.HTTP_PORT;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class HttpContainer extends GenericContainer<HttpContainer> {

    public HttpContainer(String image) {
        super(image);
        withExposedPorts(HTTP_PORT, HTTPS_PORT);
        waitingFor(Wait.forHttp("/").forPort(HTTP_PORT));
    }

    public int getHttpPort() {
        return getMappedPort(HTTP_PORT);
    }

    public int getHttpsPort() {
        return getMappedPort(HTTPS_PORT);
    }
}
