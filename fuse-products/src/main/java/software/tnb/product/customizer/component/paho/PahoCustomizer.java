package software.tnb.product.customizer.component.paho;

import software.tnb.product.customizer.ProductsCustomizer;

import java.util.Map;

public class PahoCustomizer extends ProductsCustomizer {

    private final String username;
    private final String password;
    private final String brokerUrl;

    public PahoCustomizer(String username, String password, String brokerUrl) {
        this.brokerUrl = brokerUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public void customizeCamelK() {
        customizeCEQAndCK();
    }

    @Override
    public void customizeQuarkus() {
        customizeCEQAndCK();
    }

    private void customizeCEQAndCK() {
        getIntegrationBuilder().addToProperties(Map.of(
            "camel.component.paho.username", username,
            "camel.component.paho.password", password,
            "paho.broker.tcp.url", brokerUrl
        ));
    }

    @Override
    public void customizeSpringboot() {
        getIntegrationBuilder().addToProperties(Map.of(
            "camel.component.paho.user-name", username,
            "camel.component.paho.password", password,
            "camel.component.paho.broker-url", brokerUrl
        ));
    }
}
