package org.jboss.fuse.tnb.mongodb.service;

import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.mongodb.account.MongoDBAccount;
import org.jboss.fuse.tnb.mongodb.validation.MongoDBValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;

import java.util.Map;

public abstract class MongoDB implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDB.class);
    public static final String MONGODB_IMAGE = "mongodb.image";

    private MongoDBAccount account;
    private MongoDBValidation validation;
    protected MongoClient client;

    public MongoDBAccount account() {
        if (account == null) {
            account = Accounts.get(MongoDBAccount.class);
        }
        return account;
    }

    protected abstract MongoClient client();

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
        return 27017;
    }

    public static String mongoDbImage() {
        return System.getProperty(MONGODB_IMAGE, "docker.io/bitnami/mongodb:4.4.5");
    }

    public abstract String hostname();
}
