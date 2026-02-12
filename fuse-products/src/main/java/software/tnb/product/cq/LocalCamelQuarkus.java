package software.tnb.product.cq;

import software.tnb.product.LocalProduct;
import software.tnb.product.Product;
import software.tnb.product.application.App;
import software.tnb.product.cq.application.LocalDevModeQuarkusApp;
import software.tnb.product.cq.application.LocalPackagedQuarkusApp;
import software.tnb.product.cq.integration.builder.QuarkusIntegrationBuilder;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class LocalCamelQuarkus extends LocalProduct {
    @Override
    protected App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        return integrationBuilder instanceof QuarkusIntegrationBuilder builder && builder.isDevMode()
            ? new LocalDevModeQuarkusApp(integrationBuilder)
            : new LocalPackagedQuarkusApp(integrationBuilder);
    }
}
