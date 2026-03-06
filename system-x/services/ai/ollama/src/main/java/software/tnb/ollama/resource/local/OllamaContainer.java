package software.tnb.ollama.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class OllamaContainer extends GenericContainer<OllamaContainer> {

    public OllamaContainer(String image, int port) {
        super(image);
        this.withExposedPorts(port);
        this.waitingFor(Wait.forHttp("/").forPort(port).forStatusCode(200));
    }
}
