package org.jboss.fuse.tnb.product.cq.customizer;

import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.customizer.Customizer;

public abstract class QuarkusCustomizer extends Customizer {
    public QuarkusCustomizer() {
        super(ProductType.CAMEL_QUARKUS);
    }
}
