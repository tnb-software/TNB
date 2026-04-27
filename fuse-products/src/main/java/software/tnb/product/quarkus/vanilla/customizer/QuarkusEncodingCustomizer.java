package software.tnb.product.quarkus.vanilla.customizer;

import software.tnb.product.quarkus.camel.customizer.CamelQuarkusCustomizer;
import software.tnb.product.quarkus.vanilla.configuration.QuarkusConfiguration;

/**
 * Forces the given encoding for the output.
 */
public class QuarkusEncodingCustomizer extends CamelQuarkusCustomizer {
    @Override
    public void customize() {
        getIntegrationBuilder().addToSystemProperties((QuarkusConfiguration.isQuarkusNative() ? "native" : "stdout") + ".encoding", "UTF-8");
    }
}
