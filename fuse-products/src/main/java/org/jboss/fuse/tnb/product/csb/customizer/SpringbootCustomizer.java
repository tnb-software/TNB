package org.jboss.fuse.tnb.product.csb.customizer;

import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.customizer.Customizer;

public abstract class SpringbootCustomizer extends Customizer {
    public SpringbootCustomizer() {
        super(ProductType.CAMEL_SPRINGBOOT);
    }
}
