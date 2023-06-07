package software.tnb.redis.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class RedisContainer extends GenericContainer<RedisContainer> {

    public RedisContainer(String image, int port) {
        super(image);
        this.withExposedPorts(port);
        this.waitingFor(Wait.forListeningPort());
    }
}
