package software.tnb.product.csb;

import software.tnb.product.LocalProduct;
import software.tnb.product.Product;
import software.tnb.product.application.App;
import software.tnb.product.csb.application.TomcatSpringBootApp;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class TomcatCamelSpringBoot extends LocalProduct {

    private TomcatSpringBootApp app;

    @Override
    public App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        app = new TomcatSpringBootApp(integrationBuilder);
        return app;
    }

    @Override
    public void setupProduct() {
        super.setupProduct();
    }

    @Override
    public void teardownProduct() {
        app.stop();
    }
}
