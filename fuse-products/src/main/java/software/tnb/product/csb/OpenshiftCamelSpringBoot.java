package software.tnb.product.csb;

import software.tnb.product.OpenshiftProduct;
import software.tnb.product.Product;
import software.tnb.product.application.App;
import software.tnb.product.csb.application.OpenshiftSpringBootApp;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.util.maven.Maven;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class OpenshiftCamelSpringBoot extends OpenshiftProduct {
    private static boolean initialized = false;

    @Override
    public boolean isReady() {
        return initialized;
    }

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
    protected App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        return new OpenshiftSpringBootApp(integrationBuilder);
    }
}
