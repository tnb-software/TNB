package software.tnb.product.csb.customizer;

import software.tnb.common.product.ProductType;
import software.tnb.product.customizer.Customizer;

public abstract class SpringBootCustomizer extends Customizer {
    public SpringBootCustomizer() {
        super(ProductType.CAMEL_SPRINGBOOT);
    }
}
