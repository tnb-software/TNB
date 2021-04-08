package org.jboss.fuse.tnb.product;

import org.jboss.fuse.tnb.product.util.Maven;
import org.junit.jupiter.api.extension.ExtensionContext;

public abstract class OnLocalProduct extends Product {
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        setupProduct();
    }

    public void afterAll(ExtensionContext extensionContext) throws Exception {
        teardownProduct();
    }

    @Override
    public void setupProduct() {
        Maven.setupMaven();
    }

    @Override
    public void teardownProduct() {
    }
}
