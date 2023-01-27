package software.tnb.common.deployment;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface OpenshiftDeployable extends Deployable {
    void create();

    boolean isReady();

    boolean isDeployed();

    default long waitTime() {
        return 300_000;
    }

    @Override
    default void deploy() {
        final int retries = 60;
        if (!isDeployed()) {
            create();
        }
        WaitUtils.waitFor(this::isReady, retries, waitTime() / retries,
            "Waiting until the " + this.getClass().getSimpleName() + " resource is ready");
    }

    @Override
    default void afterAll(ExtensionContext extensionContext) throws Exception {
        Deployable.super.afterAll(extensionContext);
        if (TestConfiguration.parallel()) {
            // In parallel execution, each test class has its own namespace
            OpenshiftClient.deleteNamespace();
        }
    }
}
