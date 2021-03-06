package software.tnb.product;

import software.tnb.product.util.maven.Maven;

import org.junit.jupiter.api.extension.ExtensionContext;

public abstract class LocalProduct extends Product {
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
