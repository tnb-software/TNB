package software.tnb.product.quarkus.vanilla;

import software.tnb.product.LocalProduct;
import software.tnb.product.application.App;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.quarkus.vanilla.application.LocalDevModeQuarkusApp;
import software.tnb.product.quarkus.vanilla.application.LocalPackagedQuarkusApp;
import software.tnb.product.quarkus.vanilla.integration.builder.QuarkusIntegrationBuilder;
import software.tnb.product.quarkus.vanilla.variant.QuarkusVariant;
import software.tnb.product.quarkus.vanilla.variant.VanillaQuarkusVariant;

public class LocalQuarkus extends LocalProduct {
    @Override
    protected App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        QuarkusVariant variant = quarkusVariant();
        variant.customizeIntegrationBuilder(integrationBuilder);
        return integrationBuilder instanceof QuarkusIntegrationBuilder builder && builder.isDevMode()
            ? new LocalDevModeQuarkusApp(integrationBuilder, variant)
            : new LocalPackagedQuarkusApp(integrationBuilder, variant);
    }

    protected QuarkusVariant quarkusVariant() {
        return new VanillaQuarkusVariant();
    }
}
