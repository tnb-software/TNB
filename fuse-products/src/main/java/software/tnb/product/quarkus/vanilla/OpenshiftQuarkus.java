package software.tnb.product.quarkus.vanilla;

import software.tnb.product.OpenshiftProduct;
import software.tnb.product.application.App;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.quarkus.vanilla.application.OpenshiftQuarkusApp;
import software.tnb.product.quarkus.vanilla.variant.QuarkusVariant;
import software.tnb.product.quarkus.vanilla.variant.VanillaQuarkusVariant;
import software.tnb.product.util.maven.Maven;

public class OpenshiftQuarkus extends OpenshiftProduct {
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
        QuarkusVariant quarkusVariant = quarkusVariant();
        quarkusVariant.customizeIntegrationBuilder(integrationBuilder);
        return new OpenshiftQuarkusApp(integrationBuilder, quarkusVariant);
    }

    protected QuarkusVariant quarkusVariant() {
        return new VanillaQuarkusVariant();
    }
}
