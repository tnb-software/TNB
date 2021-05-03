package org.jboss.fuse.tnb.mongodb.resource.local;

import org.jboss.fuse.tnb.common.deployment.Deployable;
import org.jboss.fuse.tnb.mongodb.service.MongoDB;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@AutoService(MongoDB.class)
public class LocalMongoDB extends MongoDB implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalMongoDB.class);
    private MongoContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting MongoDB container");
        container = new MongoContainer(mongoDbImage(), port(), containerEnvironment());
        container.start();
        LOG.info("MongoDB container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping MongoDB container");
            container.stop();
        }
    }

    @Override
    public MongoClient client() {
        if (client == null) {
            LOG.debug("Creating new MongoClient instance");
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
