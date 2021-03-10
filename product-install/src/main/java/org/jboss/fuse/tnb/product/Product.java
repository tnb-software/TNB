package org.jboss.fuse.tnb.product;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.jboss.fuse.tnb.common.deployment.Deployable;

public abstract class Product implements Deployable, BeforeAllCallback, AfterAllCallback, AfterEachCallback {
    public abstract void deployIntegration(Object route);
    public abstract void waitForIntegration();
    public abstract void undeployIntegration();

    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        deploy();
    }

    public void afterAll(ExtensionContext extensionContext) throws Exception {
        undeploy();
    }

    public void afterEach(ExtensionContext extensionContext) throws Exception {
        undeployIntegration();
    }
}
