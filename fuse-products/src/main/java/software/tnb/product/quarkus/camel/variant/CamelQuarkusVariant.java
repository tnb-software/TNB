package software.tnb.product.quarkus.camel.variant;

import static software.tnb.product.integration.builder.AbstractIntegrationBuilder.DEFAULT_STARTUP_REGEX;

import software.tnb.product.quarkus.camel.configuration.CamelQuarkusConfiguration;
import software.tnb.product.quarkus.vanilla.variant.QuarkusVariant;

/**
 * Camel Quarkus variant.
 */
public class CamelQuarkusVariant extends QuarkusVariant {
    @Override
    public String additionalBomGroupId() {
        return CamelQuarkusConfiguration.camelQuarkusPlatformGroupId();
    }

    @Override
    public String additionalBomArtifactId() {
        return CamelQuarkusConfiguration.camelQuarkusPlatformArtifactId();
    }

    @Override
    public String additionalBomVersion() {
        return CamelQuarkusConfiguration.camelQuarkusPlatformVersion();
    }

    @Override
    public String getStartupRegex() {
        // Camel uses a different startup regex than vanilla Quarkus
        return DEFAULT_STARTUP_REGEX;
    }
}
