package org.jboss.fuse.tnb.product.csb.customizer;

import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.customizer.Customizer;

public abstract class SpringBootCustomizer extends Customizer {
    public SpringBootCustomizer() {
        super(ProductType.CAMEL_SPRINGBOOT);
    }
}
