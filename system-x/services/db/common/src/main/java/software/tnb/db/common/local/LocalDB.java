package software.tnb.db.common.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.db.common.service.SQL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.util.List;

public class LocalDB implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalDB.class);
    private final SQL sqlService;
    private final WaitStrategy waitStrategy;
    private final int port;
    private DBContainer container;

    public LocalDB(SQL sqlService, int port, WaitStrategy waitStrategy) {
        this.sqlService = sqlService;
        this.port = port;
        this.waitStrategy = waitStrategy;
    }

    @Override
    public void deploy() {
        this.container = new DBContainer(sqlService, port, waitStrategy);
        LOG.info("Starting " + sqlService.name() + " container");
        container.start();
        LOG.info(sqlService.name() + " container started");
    }

    @Override
    public void undeploy() {
        container.stop();
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
}
