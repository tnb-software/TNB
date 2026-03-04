package software.tnb.product.cq.customizer;

import software.tnb.product.cq.configuration.QuarkusConfiguration;

/**
 * Forces the given encoding for the output.
 */
public class QuarkusEncodingCustomizer extends QuarkusCustomizer {
    @Override
    public void customize() {
        getIntegrationBuilder().addToSystemProperties((QuarkusConfiguration.isQuarkusNative() ? "native" : "stdout") + ".encoding", "UTF-8");
    }
}
