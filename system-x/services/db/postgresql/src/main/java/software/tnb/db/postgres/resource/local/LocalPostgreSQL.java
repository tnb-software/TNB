package software.tnb.db.postgres.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.db.common.local.DBContainer;
import software.tnb.db.common.local.LocalDB;
import software.tnb.db.postgres.service.PostgreSQL;

import org.testcontainers.containers.wait.strategy.Wait;

import com.google.auto.service.AutoService;

@AutoService(PostgreSQL.class)
public class LocalPostgreSQL extends PostgreSQL implements ContainerDeployable<DBContainer> {
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
        localDb = new LocalDB(this, PORT,
            Wait.forSuccessfulCommand("[ $(cat /var/lib/pgsql/data/userdata/log/*.log | grep \"ready to accept\" | wc -l) -eq 2 ]"));
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
