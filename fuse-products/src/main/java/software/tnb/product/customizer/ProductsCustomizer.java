package software.tnb.product.customizer;

import software.tnb.common.config.TestConfiguration;

public abstract class ProductsCustomizer extends Customizer {
    @Override
    public void customize() {
        switch (TestConfiguration.product()) {
            case CAMEL_QUARKUS:
            case CXF_QUARKUS:
                customizeQuarkus();
                break;
            case CAMEL_SPRINGBOOT:
                customizeSpringboot();
                break;
            default:
                throw new IllegalArgumentException("Implement a branch for new product in ProductsCustomizer");
        }
    }

    public abstract void customizeQuarkus();

    public abstract void customizeSpringboot();
}
