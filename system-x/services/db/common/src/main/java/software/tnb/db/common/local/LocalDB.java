package software.tnb.db.common.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.db.common.service.SQL;

import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.util.List;

public class LocalDB implements ContainerDeployable<DBContainer> {
    private final int port;
    private final DBContainer container;

    public LocalDB(SQL sqlService, int port, WaitStrategy waitStrategy) {
        this.port = port;
        this.container = new DBContainer(sqlService, port, waitStrategy);
    }

    public void restart(Runnable onContainerStopped) {
        container.getDockerClient().commitCmd(container.getContainerId())
            .withRepository("tempimg")
            .withTag("localdb").exec();
        int mappedPort = getPort();

        container.stop();

        onContainerStopped.run();

        container.setDockerImageName("tempimg:localdb");
        container.setPortBindings(List.of(mappedPort + ":" + port));
        container.start();
    }

    @Override
    public void openResources() {
        // no-op
    }

    @Override
    public void closeResources() {
        // no-op
    }

    public int getPort() {
        return container.getPort();
    }

    public String getHost() {
        return container.getHost();
    }

    @Override
    public DBContainer container() {
        return container;
    }
}
