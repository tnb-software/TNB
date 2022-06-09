package software.tnb.product.csb;

import software.tnb.product.LocalProduct;
import software.tnb.product.Product;
import software.tnb.product.application.App;
import software.tnb.product.csb.application.LocalSpringBootApp;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class LocalCamelSpringBoot extends LocalProduct {

    @Override
    public App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        return new LocalSpringBootApp(integrationBuilder);
    }
}
