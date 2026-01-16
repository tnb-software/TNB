package software.tnb.redis.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.redis.service.Redis;

import com.google.auto.service.AutoService;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

@AutoService(Redis.class)
public class LocalRedis extends Redis implements ContainerDeployable<RedisContainer> {
    private final RedisContainer container = new RedisContainer(defaultImage(), PORT);

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    @Override
    public void openResources() {
        client = RedisClient.create(RedisURI.builder()
            .withHost(host())
            .withPort(port())
            .build());
    }

    @Override
    public void closeResources() {
    }

    @Override
    public RedisContainer container() {
        return container;
    }
}
