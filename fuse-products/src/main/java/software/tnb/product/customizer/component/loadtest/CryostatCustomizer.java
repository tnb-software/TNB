package software.tnb.product.customizer.component.loadtest;

import software.tnb.product.customizer.ProductsCustomizer;

import java.util.Map;

public class CryostatCustomizer extends ProductsCustomizer {

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

    private void addJmxArgs() {
        getIntegrationBuilder().addToProperties(JMX_ARGS);
    }
}
