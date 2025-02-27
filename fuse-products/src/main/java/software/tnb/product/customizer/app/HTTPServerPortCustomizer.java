package software.tnb.product.customizer.app;

import software.tnb.product.customizer.ProductsCustomizer;

public class HTTPServerPortCustomizer extends ProductsCustomizer {
    @Override
    public void customizeQuarkus() {
        getIntegrationBuilder().addToApplicationProperties("quarkus.http.port", getIntegrationBuilder().getPort() + "");
    }

    @Override
    public void customizeSpringboot() {
        getIntegrationBuilder().addToApplicationProperties("server.port", getIntegrationBuilder().getPort() + "");
    }
}
