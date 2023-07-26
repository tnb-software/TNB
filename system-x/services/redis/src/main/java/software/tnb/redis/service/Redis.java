package software.tnb.redis.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.redis.validation.RedisValidation;

import io.lettuce.core.RedisClient;

public abstract class Redis extends Service<NoAccount, RedisClient, RedisValidation> implements WithDockerImage {

    protected static final int PORT = 6379;

    public abstract String host();

    public abstract int port();

    public RedisValidation validation() {
        if (validation == null) {
            validation = new RedisValidation(client());
        }
        return validation;
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/redis-image:6.0.20-alpine";
    }
}
