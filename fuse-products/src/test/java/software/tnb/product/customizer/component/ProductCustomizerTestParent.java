package software.tnb.product.customizer.component;

import software.tnb.common.product.ProductType;
import software.tnb.product.customizer.CustomizerTestParent;
import software.tnb.product.parent.TestParent;

import org.junit.jupiter.api.Test;

/**
 * Test parent for product customizers - for customizers that are customizing more than 1 product.
 */
public abstract class ProductCustomizerTestParent extends CustomizerTestParent {
    public abstract void validateQuarkus();

    public abstract void validateSpringBoot();

    @Override
    public ProductType product() {
        // Overriden in methods
        return ProductType.CAMEL_QUARKUS;
    }

    @Test
    public void shouldCustomizeForQuarkusTest() {
        TestParent.setProduct(ProductType.CAMEL_QUARKUS);
        validateQuarkus();
    }

    @Test
    public void shouldCustomizeForSpringBootTest() {
        TestParent.setProduct(ProductType.CAMEL_SPRINGBOOT);
        validateSpringBoot();
    }
}
