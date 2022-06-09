package software.tnb.product.parent;

import software.tnb.common.product.ProductType;

import org.junit.jupiter.api.BeforeEach;

/**
 * Test parent for tests for a given product.
 */
public abstract class ProductTestParent extends TestParent {
    public abstract ProductType product();

    @BeforeEach
    public void setProduct() {
        setProduct(product());
    }
}
