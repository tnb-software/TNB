package org.jboss.fuse.tnb.http.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class HttpContainer extends GenericContainer<HttpContainer> {

    private static final int HTTP_PORT = 8000;
    private static final int HTTPS_PORT = 8443;

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
