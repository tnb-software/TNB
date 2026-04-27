package software.tnb.product.quarkus.vanilla.customizer;

import software.tnb.product.customizer.Customizer;
import software.tnb.product.quarkus.vanilla.configuration.QuarkusConfiguration;

/**
 * Forces the given encoding for the output.
 */
public class QuarkusEncodingCustomizer extends Customizer {
    @Override
    public void customize() {
        getIntegrationBuilder().addToSystemProperties((QuarkusConfiguration.isQuarkusNative() ? "native" : "stdout") + ".encoding", "UTF-8");
    }
}
