package software.tnb.flink.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.flink.service.Flink;
import software.tnb.flink.service.configuration.FlinkConfiguration;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;

@AutoService(Flink.class)
public class OpenshiftFlink extends Flink implements OpenshiftDeployable, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftFlink.class);
    private OpenshiftDeployable delegate;

    public OpenshiftDeployable selectOpenshiftFlinkService() {
        OpenshiftDeployable deployable = new OpenshiftFlinkOperator();
        if (getCurrentForcingConfiguration()) {
            deployable = new OpenshiftFlinkImage();
            ((FlinkConfiguration) ((OpenshiftFlinkImage) deployable).getConfiguration()).forceUseImageServer(true);
        } else {
            ((FlinkConfiguration) ((OpenshiftFlink) deployable).getConfiguration()).forceUseImageServer(false);
        }
        return deployable;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        this.delegate = selectOpenshiftFlinkService();
        OpenshiftDeployable.super.beforeAll(extensionContext);
    }

    @Override
    public void undeploy() {
        this.delegate.undeploy();
    }

    @Override
    public void openResources() {
        this.delegate.openResources();
    }

    @Override
    public void closeResources() {
        this.delegate.closeResources();
    }

    @Override
    public void create() {
        this.delegate.create();
    }

    @Override
    public boolean isDeployed() {
        return this.delegate.isDeployed();
    }

    @Override
    public String name() {
        return "tnb-flink";
    }

    @Override
    public Predicate<Pod> podSelector() {
        return this.delegate.podSelector();
    }

    @Override
    public String host() {
        return getCurrentForcingConfiguration() ? "jobmanager" : name() + "-rest";
    }

    @Override
    public int port() {
        return PORT;
    }
}
