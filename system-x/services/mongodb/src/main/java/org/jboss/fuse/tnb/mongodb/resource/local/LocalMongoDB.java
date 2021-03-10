package org.jboss.fuse.tnb.mongodb.resource.local;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.jboss.fuse.tnb.common.deployment.Deployable;
import org.jboss.fuse.tnb.mongodb.service.MongoDB;

import com.google.auto.service.AutoService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@AutoService(MongoDB.class)
public class LocalMongoDB extends MongoDB implements Deployable {
    private MongoContainer container;

    @Override
    public void deploy() {
        container = new MongoContainer(image(), port(), containerEnvironment());
        container.start();
    }

    @Override
    public void undeploy() {
        if (container != null) {
            container.stop();
        }
    }

    @Override
    public MongoClient client() {
        if (client == null) {
            client = MongoClients.create(replicaSetUrl());
        }
        return client;
    }

    @Override
    public String replicaSetUrl() {
        return String.format("mongodb://%s:%s@%s:%d/%s", account().username(), account().password(), container.getContainerIpAddress(),
            container.getPort(), account().database());
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        undeploy();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        deploy();
    }
}
