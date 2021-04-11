package org.jboss.fuse.tnb.product;

import com.squareup.javapoet.CodeBlock;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;


public abstract class Product implements BeforeAllCallback, AfterAllCallback, AfterEachCallback {
    public abstract void createIntegration(String name, CodeBlock routeDefinition, String... camelComponents);
    public abstract void waitForIntegration(String name);
    public abstract void removeIntegration();
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        removeIntegration();
    }
    public abstract void setupProduct();

    public abstract void teardownProduct();
}
