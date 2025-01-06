package software.tnb.product.customizer.component.pahomqtt5;

import software.tnb.product.customizer.ProductsCustomizer;

import java.util.Map;

public class PahoCustomizerMQTT5 extends ProductsCustomizer {

    private final String username;
    private final String password;
    private final String brokerUrl;

    public PahoCustomizerMQTT5(String username, String password, String brokerUrl) {
        this.brokerUrl = brokerUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public void customizeQuarkus() {
        customizeCEQAndCK();
    }

    private void customizeCEQAndCK() {
        getIntegrationBuilder().addToApplicationProperties(Map.of(
            "camel.component.paho-mqtt5.username", username,
            "camel.component.paho-mqtt5.password", password,
            "paho-mqtt5.broker.tcp.url", brokerUrl
        ));
    }

    @Override
    public void customizeSpringboot() {
        getIntegrationBuilder().addToApplicationProperties(Map.of(
            "camel.component.paho-mqtt5.user-name", username,
            "camel.component.paho-mqtt5.password", password,
            "camel.component.paho-mqtt5.broker-url", brokerUrl
        ));
    }
}
