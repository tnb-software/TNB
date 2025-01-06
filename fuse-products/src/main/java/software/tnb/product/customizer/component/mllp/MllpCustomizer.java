package software.tnb.product.customizer.component.mllp;

import software.tnb.product.customizer.ProductsCustomizer;

public class MllpCustomizer extends ProductsCustomizer {

    private final int mllpPort;

    public MllpCustomizer(int mllpPort) {
        this.mllpPort = mllpPort;
    }

    @Override
    public void customizeQuarkus() {
        customizeCEQAndCK();
    }

    @Override
    public void customizeSpringboot() {

    }

    private void customizeCEQAndCK() {
        this.getIntegrationBuilder().addToApplicationProperties("quarkus.openshift.ports.\"ports\".container-port", String.valueOf(mllpPort));
    }
}
