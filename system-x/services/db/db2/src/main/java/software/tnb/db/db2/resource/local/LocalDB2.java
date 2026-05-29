package software.tnb.db.db2.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.db.common.local.DBContainer;
import software.tnb.db.common.local.LocalDB;
import software.tnb.db.db2.service.DB2;

import org.testcontainers.containers.wait.strategy.Wait;

import com.google.auto.service.AutoService;

import java.time.Duration;

@AutoService(DB2.class)
public class LocalDB2 extends DB2 implements ContainerDeployable<DBContainer> {
    private LocalDB localDb;

    @Override
    public DBContainer container() {
        return localDb.container();
    }

    @Override
    public void deploy() {
        localDb = new LocalDB(this, PORT,
            Wait.forLogMessage(".*Setup has completed.*", 1)
                .withStartupTimeout(Duration.ofMinutes(5)));
        localDb.container().withPrivilegedMode(true);
        localDb.deploy();
    }

    @Override
    public void undeploy() {
        localDb.undeploy();
    }

    @Override
    public void openResources() {
        localDb.openResources();
    }

    @Override
    public void closeResources() {
        localDb.closeResources();
        validation = null;
    }

    @Override
    public String host() {
        return localDb.getHost();
    }

    @Override
    public int port() {
        return localDb.getPort();
    }

    @Override
    public void restart(Runnable onContainerStopped) {
        localDb.restart(onContainerStopped);
    }
}
