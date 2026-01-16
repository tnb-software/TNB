package software.tnb.db.mongodb.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.db.mongodb.service.MongoDB;

import com.google.auto.service.AutoService;

@AutoService(MongoDB.class)
public class LocalMongoDB extends MongoDB implements ContainerDeployable<MongoDBContainer> {
    private final MongoDBContainer container = new MongoDBContainer(image(), PORT, containerEnvironment());

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    @Override
    public MongoDBContainer container() {
        return container;
    }
}
