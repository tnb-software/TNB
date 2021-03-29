package org.jboss.fuse.tnb.mongodb.service;

import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.mongodb.account.MongoDBAccount;
import org.jboss.fuse.tnb.mongodb.validation.MongoDBValidation;

import com.mongodb.client.MongoClient;

import java.util.Map;

public abstract class MongoDB implements Service {
    private MongoDBAccount account;
    private MongoDBValidation validation;
    protected MongoClient client;

    public MongoDBAccount account() {
        if (account == null) {
            MongoDBAccount account = new MongoDBAccount();
            account.setUsername("user");
            account.setPassword("user");
            account.setDatabase("sampledb");
            account.setReplicaSetName("rs0");
            account.setReplicaSetMode("primary");
            account.setReplicaSetKey("replica");
            account.setRootPassword("admin");
            this.account = account;
        }
        return account;
    }

    public abstract MongoClient client();

    public MongoDBValidation validation() {
        if (validation == null) {
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

    public String image() {
        return "quay.io/bitnami/mongodb:3.6.16";
    }
}
