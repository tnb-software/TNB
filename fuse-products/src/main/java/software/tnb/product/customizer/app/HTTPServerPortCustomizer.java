package software.tnb.product.customizer.app;

import software.tnb.product.customizer.ProductsCustomizer;

public class HTTPServerPortCustomizer extends ProductsCustomizer {
    private final boolean configure;

    public HTTPServerPortCustomizer(boolean configure) {
        this.configure = configure;
    }

    @Override
    public void customizeQuarkus() {
        if (configure) {
            getIntegrationBuilder().addToApplicationProperties("quarkus.http.port", getIntegrationBuilder().getPort() + "");
        }
    }

    @Override
    public void customizeSpringboot() {
        if (configure) {
            getIntegrationBuilder().addToApplicationProperties("server.port", getIntegrationBuilder().getPort() + "");
        }
    }
}
