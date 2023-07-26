package software.tnb.redis.validation;

import software.tnb.common.validation.Validation;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

public class RedisValidation implements Validation {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;

    public RedisValidation(RedisClient redisClient) {
        this.redisClient = redisClient;
        this.connection = redisClient.connect();
    }

    public void pushToChannel(String message, String channel) {
        redisClient.connectPubSub().sync().publish(channel, message);
    }

    public String getValue(String key) {
        return connection.sync().get(key);
    }
}
