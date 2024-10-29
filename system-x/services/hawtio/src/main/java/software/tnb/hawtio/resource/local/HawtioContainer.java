package software.tnb.hawtio.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class HawtioContainer extends GenericContainer<HawtioContainer> {

    public HawtioContainer(String image, int port) {
        super(image);
        this.setCommandParts(new String[] {"--port", "" + port});
        this.waitingFor(Wait.forSuccessfulCommand("cat < /dev/null > /dev/tcp/localhost/" + port));
        this.withNetworkMode("host"); // using host network
    }
}
