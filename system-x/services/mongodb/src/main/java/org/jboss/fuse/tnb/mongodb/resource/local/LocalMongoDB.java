package org.jboss.fuse.tnb.mongodb.resource.local;

import org.jboss.fuse.tnb.common.deployment.Deployable;
import org.jboss.fuse.tnb.mongodb.service.MongoDB;

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
    public void openResources() {
        LOG.debug("Creating new MongoClient instance");
        client = MongoClients.create(replicaSetUrl());
    }

    @Override
    public void closeResources() {
        if (client != null) {
            LOG.debug("Closing MongoDB client");
            client.close();
        }
    }

    @Override
    protected MongoClient client() {
        return client;
    }

    @Override
    public String replicaSetUrl() {
        return String.format("mongodb://%s:%s@%s:%d/%s", account().username(), account().password(), container.getContainerIpAddress(),
            container.getPort(), account().database());
    }
}
