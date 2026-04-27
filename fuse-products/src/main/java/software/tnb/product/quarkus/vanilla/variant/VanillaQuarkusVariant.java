package software.tnb.product.quarkus.vanilla.variant;

import software.tnb.product.quarkus.vanilla.configuration.QuarkusConfiguration;

/**
 * Vanilla Quarkus variant without Camel.
 */
public class VanillaQuarkusVariant implements QuarkusVariant {
    @Override
    public String additionalBomGroupId() {
        return QuarkusConfiguration.quarkusPlatformGroupId();
    }

    @Override
    public String additionalBomArtifactId() {
        return QuarkusConfiguration.quarkusPlatformArtifactId();
    }

    @Override
    public String additionalBomVersion() {
        return QuarkusConfiguration.quarkusPlatformVersion();
    }
}
