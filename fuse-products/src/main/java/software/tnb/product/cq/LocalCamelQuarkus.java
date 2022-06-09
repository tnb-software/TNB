package software.tnb.product.cq;

import software.tnb.product.LocalProduct;
import software.tnb.product.Product;
import software.tnb.product.application.App;
import software.tnb.product.cq.application.LocalQuarkusApp;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class LocalCamelQuarkus extends LocalProduct {
    @Override
    protected App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        return new LocalQuarkusApp(integrationBuilder);
    }
}
