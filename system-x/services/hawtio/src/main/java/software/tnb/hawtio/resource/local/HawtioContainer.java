package software.tnb.hawtio.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class HawtioContainer extends GenericContainer<HawtioContainer> {

    public HawtioContainer(String image, int port) {
        super(image);
        this.setCommandParts(new String[] {
            "--port", String.valueOf(port),
            // needed for connection to Hawtio from different machine (ie. for sidecar docker)
            "--host", "0.0.0.0"
        });
        // needed for proxy redirection from Hawtio to different machine (ie. for sidecar docker)
        this.withEnv("JDK_JAVA_OPTIONS", "-Dhawtio.proxyAllowlist=*");
        this.waitingFor(Wait.forSuccessfulCommand("cat < /dev/null > /dev/tcp/localhost/" + port));
        this.withNetworkMode("host"); // using host network
    }
}
