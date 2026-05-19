package software.tnb.product.quarkus.camel.variant;

import static software.tnb.product.integration.builder.AbstractIntegrationBuilder.DEFAULT_STARTUP_REGEX;

import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.quarkus.vanilla.configuration.QuarkusConfiguration;
import software.tnb.product.quarkus.vanilla.variant.QuarkusVariant;

/**
 * Camel Quarkus variant.
 */
public class CamelQuarkusVariant implements QuarkusVariant {
    @Override
    public String additionalBomGroupId() {
        return QuarkusConfiguration.camelQuarkusPlatformGroupId();
    }

    @Override
    public String additionalBomArtifactId() {
        return QuarkusConfiguration.camelQuarkusPlatformArtifactId();
    }

    @Override
    public String additionalBomVersion() {
        return QuarkusConfiguration.camelQuarkusPlatformVersion();
    }

    @Override
    public void customizeIntegrationBuilder(AbstractIntegrationBuilder<?> integrationBuilder) {
        integrationBuilder.startupRegex(DEFAULT_STARTUP_REGEX);
    }
}
