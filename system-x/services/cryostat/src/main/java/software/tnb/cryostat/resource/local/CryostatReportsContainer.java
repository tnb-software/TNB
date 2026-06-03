package software.tnb.cryostat.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

public class CryostatReportsContainer extends GenericContainer<CryostatReportsContainer> {

    static final int PORT = 8080;

    public CryostatReportsContainer(String image) {
        super(image);
        this.withExposedPorts(PORT);
        this.waitingFor(Wait.forLogMessage(".*Listening on.*", 1)
            .withStartupTimeout(Duration.ofMinutes(2)));
    }
}
