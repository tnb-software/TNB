package software.tnb.db.mssql.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.db.common.local.DBContainer;
import software.tnb.db.common.local.LocalDB;
import software.tnb.db.mssql.service.MSSQL;

import org.testcontainers.containers.wait.strategy.Wait;

import com.google.auto.service.AutoService;

@AutoService(MSSQL.class)
public class LocalMSSQL extends MSSQL implements ContainerDeployable<DBContainer> {
    private LocalDB localDb;

    @Override
    public String host() {
        return localDb.getHost();
    }

    @Override
    public int port() {
        return localDb.getPort();
    }

    @Override
    public DBContainer container() {
        return localDb.container();
    }

    @Override
    public void deploy() {
        // the container environment is in the configuration, therefore we must delay creating the local db instance
        localDb = new LocalDB(this, PORT, Wait.forLogMessage(".*Recovery is complete.*", 1));
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
    public void restart(Runnable onContainerStopped) {
        localDb.restart(onContainerStopped);
    }
}
