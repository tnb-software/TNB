package org.jboss.fuse.tnb.common.deployment;

import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.WaitUtils;

public interface OpenshiftDeployable extends Deployable {
    void create();

    boolean isReady();

    boolean isDeployed();

    @Override
    default void deploy() {
        if (!isDeployed()) {
            OpenshiftClient.get().createNamespace(); // ensure namespace exists
            create();
        }
        WaitUtils.waitFor(this::isReady, 60, 5000L, "Waiting until the " + this.getClass().getSimpleName() + " resource is ready");
    }
}
