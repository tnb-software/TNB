package software.tnb.http.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import software.tnb.http.service.HTTP;

public class HTTPContainer extends GenericContainer<HTTPContainer> {

    public HTTPContainer(String image) {
        super(image);
        withExposedPorts(HTTP.HTTP_PORT, HTTP.HTTPS_PORT);
        waitingFor(Wait.forHttp("/").forPort(HTTP.HTTP_PORT));
    }

    public int getHttpPort() {
        return getMappedPort(HTTP.HTTP_PORT);
    }

    public int getHttpsPort() {
        return getMappedPort(HTTP.HTTPS_PORT);
    }
}
