package software.tnb.db.mssql.resource.remote;

import software.tnb.common.deployment.RemoteService;
import software.tnb.db.mssql.service.MSSQL;

import com.google.auto.service.AutoService;

@AutoService(MSSQL.class)
public class RemoteMSSQL extends MSSQL implements RemoteService {
    @Override
    public String host() {
        return RemoteService.super.host();
    }

    @Override
    protected String localConnectionUrl() {
        return jdbcConnectionUrl();
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }
}
