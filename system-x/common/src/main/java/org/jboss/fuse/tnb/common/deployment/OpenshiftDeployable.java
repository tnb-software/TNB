package org.jboss.fuse.tnb.common.deployment;

import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface OpenshiftDeployable extends Deployable {
    void create();

    boolean isReady();

    boolean isDeployed();

    @Override
    default void deploy() {
        if (!isDeployed()) {
            OpenshiftClient.createNamespace(); // ensure namespace exists
            create();
            WaitUtils.waitFor(this::isReady, 60, 5000L, "Waiting until the " + this.getClass().getSimpleName() + " resource is ready");
        }
    }

    @Override
    default void beforeAll(ExtensionContext extensionContext) throws Exception {
        deploy();
    }

    @Override
    default void afterAll(ExtensionContext extensionContext) throws Exception {
        undeploy();
    }
}
