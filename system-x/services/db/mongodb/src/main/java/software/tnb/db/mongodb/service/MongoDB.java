package software.tnb.db.mongodb.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.db.mongodb.account.MongoDBAccount;
import software.tnb.db.mongodb.validation.MongoDBValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.Map;

public abstract class MongoDB extends Service<MongoDBAccount, MongoClient, MongoDBValidation> implements WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDB.class);
    protected static final int PORT = 27017;

    public abstract String host();

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

    public String replicaSetUrl() {
        return String.format("mongodb://%s:%s@%s:%d/%s", account().username(), account().password(), host(), port(), account().database());
    }

    public int port() {
        return PORT;
    }

    public String defaultImage() {
        return "quay.io/fuse_qe/mongodb:4.4.15";
    }

    public void openResources() {
        LOG.debug("Creating new MongoClient instance");
        client = MongoClients.create(replicaSetUrl());
    }

    public void closeResources() {
        if (client != null) {
            LOG.debug("Closing MongoDB client");
            client.close();
        }
        validation = null;
    }
}
