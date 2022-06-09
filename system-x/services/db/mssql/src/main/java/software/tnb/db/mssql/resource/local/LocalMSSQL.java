package software.tnb.db.mssql.resource.local;

import software.tnb.db.mssql.service.MSSQL;

import software.tnb.common.deployment.Deployable;
import software.tnb.db.common.local.LocalDB;

import org.testcontainers.containers.wait.strategy.Wait;

import com.google.auto.service.AutoService;

@AutoService(MSSQL.class)
public class LocalMSSQL extends MSSQL implements Deployable {
    private final LocalDB localDb = new LocalDB(this, PORT, Wait.forLogMessage(".*Recovery is complete.*", 1));

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
        return "MSSQL";
    }
}
