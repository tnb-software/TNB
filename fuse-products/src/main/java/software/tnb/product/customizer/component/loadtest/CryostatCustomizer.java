package software.tnb.product.customizer.component.loadtest;

import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.customizer.ProductsCustomizer;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import java.util.Map;

import io.fabric8.camelk.v1.IntegrationSpecBuilder;

public class CryostatCustomizer extends ProductsCustomizer implements IntegrationSpecCustomizer {

    public static final Map<String, String> JMX_ARGS = Map.of("com.sun.management.jmxremote", "true"
        , "com.sun.management.jmxremote.port", "9096"
        , "com.sun.management.jmxremote.authenticate", "false"
        , "com.sun.management.jmxremote.ssl", "false");

    @Override
    public void customizeCamelK() {
    }

    @Override
    public void customizeQuarkus() {
        addJmxArgs();
    }

    @Override
    public void customizeSpringboot() {
        addJmxArgs();
    }

    private AbstractIntegrationBuilder<?> addJmxArgs() {
        return getIntegrationBuilder().addToProperties(JMX_ARGS);
    }

    @Override
    public void customizeIntegration(IntegrationSpecBuilder integrationSpecBuilder) {

    }
}
