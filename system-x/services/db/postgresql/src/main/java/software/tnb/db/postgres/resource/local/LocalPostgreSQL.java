package software.tnb.db.postgres.resource.local;

import software.tnb.db.postgres.service.PostgreSQL;
import software.tnb.common.deployment.Deployable;
import software.tnb.db.common.local.LocalDB;

import org.testcontainers.containers.wait.strategy.Wait;

import com.google.auto.service.AutoService;

@AutoService(PostgreSQL.class)
public class LocalPostgreSQL extends PostgreSQL implements Deployable {
    private final LocalDB localDb = new LocalDB(this, PORT, Wait.forLogMessage(".*Future log output will appear in directory.*", 2));

    @Override
    public String hostname() {
        return "localhost";
    }

    @Override
    public int port() {
        return localDb.getPort();
    }

    @Override
    public void deploy() {
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
    }

    @Override
    public String name() {
        return "PostgreSQL";
    }
}
