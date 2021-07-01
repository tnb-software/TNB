package org.jboss.fuse.tnb.sql.mariadb.resource.openshift;

import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.common.deployment.WithName;
import org.jboss.fuse.tnb.sql.common.resource.openshift.GenericOpenshiftDb;
import org.jboss.fuse.tnb.sql.mariadb.service.MariaDb;

import com.google.auto.service.AutoService;

@AutoService(MariaDb.class)
public class OpenshiftMariaDb extends MariaDb implements OpenshiftDeployable, WithName {

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
        return name();
    }

    @Override
    public String name() {
        return "mariadb-tnb";
    }

    @Override
    protected String jdbcConnectionUrl() {
        return String.format("jdbc:mariadb://localhost:%d/%s", port(), account().database());
    }
}
