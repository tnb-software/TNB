package software.tnb.product.quarkus.camel.customizer;

import software.tnb.common.product.ProductType;
import software.tnb.product.customizer.Customizer;

public abstract class CamelQuarkusCustomizer extends Customizer {
    public CamelQuarkusCustomizer() {
        super(ProductType.CAMEL_QUARKUS);
    }
}
