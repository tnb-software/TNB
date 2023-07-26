package software.tnb.redis.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.redis.service.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

@AutoService(Redis.class)
public class LocalRedis extends Redis implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalRedis.class);
    private static final int PORT = 6379;
    private RedisContainer redisContainer;

    @Override
    public void deploy() {
        LOG.info("Starting Redis container");
        redisContainer = new RedisContainer(defaultImage(), PORT);
        redisContainer.start();
        LOG.info("Redis container started");
    }

    @Override
    public void undeploy() {
        if (redisContainer != null) {
            LOG.info("Stopping Redis container");
            redisContainer.stop();
        }
    }

    @Override
    public String host() {
        return redisContainer.getHost();
    }

    @Override
    public int port() {
        return redisContainer.getMappedPort(PORT);
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
}
