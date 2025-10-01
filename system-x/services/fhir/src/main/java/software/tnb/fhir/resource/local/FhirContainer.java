package software.tnb.fhir.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Map;

public class FhirContainer extends GenericContainer<FhirContainer> {

    public FhirContainer(String image, int port, Map<String, String> env) {
        super(image);
        withExposedPorts(port);
        withEnv(env);
        waitingFor(Wait.forLogMessage(".*Finished.*bulk.*job.*", 1).withStartupTimeout(Duration.ofSeconds(240)));
    }
}
