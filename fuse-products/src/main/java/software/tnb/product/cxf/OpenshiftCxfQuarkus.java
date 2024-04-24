package software.tnb.product.cxf;

import software.tnb.product.OpenshiftProduct;
import software.tnb.product.Product;
import software.tnb.product.application.App;
import software.tnb.product.cxf.app.PlainQuarkusApp;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.util.maven.Maven;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class OpenshiftCxfQuarkus extends OpenshiftProduct {
    private static boolean initialized = false;

    @Override
    public void setupProduct() {
        if (!initialized) {
            Maven.setupMaven();
            initialized = true;
        }
    }

    @Override
    public void teardownProduct() {

    }

    @Override
    public boolean isReady() {
        return initialized;
    }

    @Override
    protected App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        return new PlainQuarkusApp(integrationBuilder);
    }
}
