package software.tnb.product.cq.customizer;

import software.tnb.common.product.ProductType;
import software.tnb.product.customizer.Customizer;

public abstract class QuarkusCustomizer extends Customizer {
    public QuarkusCustomizer() {
        super(ProductType.CAMEL_QUARKUS);
    }
}
