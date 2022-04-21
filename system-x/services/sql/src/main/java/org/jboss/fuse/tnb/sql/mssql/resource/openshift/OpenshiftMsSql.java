package org.jboss.fuse.tnb.sql.mssql.resource.openshift;

import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.common.deployment.WithName;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.sql.common.resource.openshift.GenericOpenshiftDb;
import org.jboss.fuse.tnb.sql.mssql.service.MsSql;

import com.google.auto.service.AutoService;

@AutoService(MsSql.class)
public class OpenshiftMsSql extends MsSql implements OpenshiftDeployable, WithName {

    private GenericOpenshiftDb openshiftDb = new GenericOpenshiftDb(this);

    @Override
    public void create() {
        openshiftDb.create();
    }

    @Override
    public void undeploy() {
        openshiftDb.undeploy();
    }

    @Override
    public void openResources() {
        openshiftDb.openResources();
    }

    @Override
    public void closeResources() {
        openshiftDb.closeResources();
    }

    @Override
    public boolean isReady() {
        return openshiftDb.isReady();
    }

    @Override
    public boolean isDeployed() {
        return openshiftDb.isDeployed();
    }

    @Override
    public String hostname() {
        return OpenshiftClient.get().getClusterHostname(name());
    }

    @Override
    public String name() {
        return "mssql-tnb";
    }

    @Override
    protected String jdbcConnectionUrl() {
        return String.format("jdbc:sqlserver://localhost:%d;databaseName=%s", port(), account().database());
    }
}
