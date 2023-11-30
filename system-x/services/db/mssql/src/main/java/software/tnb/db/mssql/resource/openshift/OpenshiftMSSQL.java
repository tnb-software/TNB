package software.tnb.db.mssql.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.db.common.openshift.OpenshiftDB;
import software.tnb.db.mssql.service.MSSQL;

import com.google.auto.service.AutoService;

import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;

@AutoService(MSSQL.class)
public class OpenshiftMSSQL extends MSSQL implements OpenshiftDeployable, WithName, WithInClusterHostname {
    private final OpenshiftDB openshiftDb = new OpenshiftDB(this, PORT);

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
        validation = null;
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
    public Predicate<Pod> podSelector() {
        return openshiftDb.podSelector();
    }

    @Override
    public String host() {
        return inClusterHostname();
    }

    @Override
    public int localPort() {
        return openshiftDb.localPort();
    }
}
