package software.tnb.common.deployment;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;

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
            OpenshiftClient.get().createNamespace(); // ensure namespace exists
            create();
        }
        WaitUtils.waitFor(this::isReady, retries, waitTime() / retries,
            "Waiting until the " + this.getClass().getSimpleName() + " resource is ready");
    }
}
