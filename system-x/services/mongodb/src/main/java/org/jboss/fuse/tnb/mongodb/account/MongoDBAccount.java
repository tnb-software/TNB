package org.jboss.fuse.tnb.mongodb.account;

import org.jboss.fuse.tnb.common.account.Account;

public class MongoDBAccount implements Account {
    private String username = "user";
    private String password = "user";
    private String database = "sampledb";
    private String replicaSetUrl;
    private String replicaSetName = "rs0";
    private String replicaSetMode = "primary";
    private String replicaSetKey = "replica";
    private String rootPassword = "admin";

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setReplicaSetUrl(String replicaSetUrl) {
        this.replicaSetUrl = replicaSetUrl;
    }

    public void setReplicaSetName(String replicaSetName) {
        this.replicaSetName = replicaSetName;
    }

    public void setReplicaSetMode(String replicaSetMode) {
        this.replicaSetMode = replicaSetMode;
    }

    public void setReplicaSetKey(String replicaSetKey) {
        this.replicaSetKey = replicaSetKey;
    }

    public void setRootPassword(String rootPassword) {
        this.rootPassword = rootPassword;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String database() {
        return database;
    }

    public String replicaSetUrl() {
        return replicaSetUrl;
    }

    public String replicaSetName() {
        return replicaSetName;
    }

    public String replicaSetMode() {
        return replicaSetMode;
    }

    public String replicaSetKey() {
        return replicaSetKey;
    }

    public String rootPassword() {
        return rootPassword;
    }
}
