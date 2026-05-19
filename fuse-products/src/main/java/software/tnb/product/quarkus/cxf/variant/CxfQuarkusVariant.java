package software.tnb.product.quarkus.cxf.variant;

import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.quarkus.vanilla.configuration.QuarkusConfiguration;
import software.tnb.product.quarkus.vanilla.variant.QuarkusVariant;

/**
 * CXF Quarkus variant.
 */
public class CxfQuarkusVariant implements QuarkusVariant {
    @Override
    public String additionalBomGroupId() {
        return QuarkusConfiguration.cxfQuarkusPlatformGroupId();
    }

    @Override
    public String additionalBomArtifactId() {
        return QuarkusConfiguration.cxfQuarkusPlatformArtifactId();
    }

    @Override
    public String additionalBomVersion() {
        return QuarkusConfiguration.cxfQuarkusPlatformVersion();
    }

    @Override
    public String getExtensions() {
        // CXF Quarkus needs smallrye-health for proper readiness probes
        String base = QuarkusVariant.super.getExtensions();
        return base.isEmpty() ? "smallrye-health" : base + ",smallrye-health";
    }

    @Override
    public void customizeIntegrationBuilder(AbstractIntegrationBuilder<?> integrationBuilder) {
        QuarkusVariant.super.customizeIntegrationBuilder(integrationBuilder);
        integrationBuilder.dependencies("io.quarkiverse.cxf:quarkus-cxf");
    }
}
