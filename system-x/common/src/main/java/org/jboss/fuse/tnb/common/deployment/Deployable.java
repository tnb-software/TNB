package org.jboss.fuse.tnb.common.deployment;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;

public interface Deployable extends BeforeAllCallback, AfterAllCallback {
    void deploy();

    void undeploy();
}
