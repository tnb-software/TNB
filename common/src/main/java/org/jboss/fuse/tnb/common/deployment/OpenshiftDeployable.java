package org.jboss.fuse.tnb.common.deployment;

import org.jboss.fuse.tnb.common.utils.WaitUtils;

import java.util.concurrent.TimeoutException;

public interface OpenshiftDeployable extends Deployable {
    void create();
    boolean isReady();
    boolean isDeployed();

    @Override
    default void deploy() {
        create();
        try {
            WaitUtils.waitFor(this::isReady, 60, 5000L);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
