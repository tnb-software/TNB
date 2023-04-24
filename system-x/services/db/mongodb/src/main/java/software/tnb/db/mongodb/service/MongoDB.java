package software.tnb.db.mongodb.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.db.mongodb.account.MongoDBAccount;
import software.tnb.db.mongodb.validation.MongoDBValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;

import java.util.Map;

public abstract class MongoDB extends Service<MongoDBAccount, MongoClient, MongoDBValidation> implements WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDB.class);
    public static final int DEFAULT_PORT = 27017;

    public MongoDBValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new MongoDB validation");
            validation = new MongoDBValidation(client(), account());
        }
        return validation;
    }

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "MONGODB_USERNAME", account().username(),
            "MONGODB_PASSWORD", account().password(),
            "MONGODB_DATABASE", account().database(),
            "MONGODB_REPLICA_SET_NAME", account().replicaSetName(),
            "MONGODB_REPLICA_SET_MODE", account().replicaSetMode(),
            "MONGODB_REPLICA_SET_KEY", account().replicaSetKey(),
            "MONGODB_ROOT_PASSWORD", account().rootPassword()
        );
    }

    public abstract String replicaSetUrl();

    public int port() {
        return DEFAULT_PORT;
    }

    public String defaultImage() {
        return "quay.io/fuse_qe/mongodb:4.4.5";
    }

    public abstract String hostname();
}
