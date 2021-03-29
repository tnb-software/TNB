package org.jboss.fuse.tnb.common.deployment;

import org.jboss.fuse.tnb.common.utils.WaitUtils;

public interface OpenshiftDeployable extends Deployable {
    void create();

    boolean isReady();

    boolean isDeployed();

    @Override
    default void deploy() {
        create();
        WaitUtils.waitFor(this::isReady, 60, 5000L, "Waiting until the resource is ready");
    }
}
