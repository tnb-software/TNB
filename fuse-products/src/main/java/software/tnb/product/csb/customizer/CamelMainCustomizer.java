package software.tnb.product.csb.customizer;

public class CamelMainCustomizer extends SpringBootCustomizer {
    @Override
    public void customize() {
        getIntegrationBuilder().addToProperties("camel.springboot.main-run-controller", "true");
    }
}
