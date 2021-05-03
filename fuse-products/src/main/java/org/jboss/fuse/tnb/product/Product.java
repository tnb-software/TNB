package org.jboss.fuse.tnb.product;

import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public abstract class Product implements BeforeAllCallback, AfterAllCallback, AfterEachCallback {
    public abstract App createIntegration(IntegrationBuilder integrationBuilder);

    public abstract void removeIntegrations();

    public void afterEach(ExtensionContext extensionContext) throws Exception {
        removeIntegrations();
    }

    public abstract void setupProduct();

    public abstract void teardownProduct();
}
