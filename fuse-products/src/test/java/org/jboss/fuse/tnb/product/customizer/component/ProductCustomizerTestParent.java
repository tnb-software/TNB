package org.jboss.fuse.tnb.product.customizer.component;

import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.customizer.CustomizerTestParent;

import org.junit.jupiter.api.Test;

/**
 * Test parent for product customizers - for customizers that are customizing more than 1 product.
 */
public abstract class ProductCustomizerTestParent extends CustomizerTestParent {
    public abstract void validateQuarkus();

    public abstract void validateCamelK();

    public abstract void validateSpringBoot();

    @Override
    public ProductType product() {
        // Overriden in methods
        return ProductType.CAMEL_QUARKUS;
    }

    @Test
    public void shouldCustomizeForQuarkusTest() {
        setProduct(ProductType.CAMEL_QUARKUS);
        validateQuarkus();
    }

    @Test
    public void shouldCustomizeForCamelKTest() {
        setProduct(ProductType.CAMEL_K);
        validateCamelK();
    }

    @Test
    public void shouldCustomizeForSpringBootTest() {
        setProduct(ProductType.CAMEL_SPRINGBOOT);
        validateSpringBoot();
    }
}
