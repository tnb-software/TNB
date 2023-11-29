package software.tnb.db.mongodb.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.db.mongodb.service.MongoDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(MongoDB.class)
public class LocalMongoDB extends MongoDB implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalMongoDB.class);
    private MongoContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting MongoDB container");
        container = new MongoContainer(image(), DEFAULT_PORT, containerEnvironment());
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
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(DEFAULT_PORT);
    }
}
