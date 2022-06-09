package software.tnb.product.customizer;

import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.parent.ProductTestParent;

import org.junit.jupiter.api.BeforeEach;

/**
 * Test parent for customizer tests.
 *
 * Handles the initialization of a tested customizer
 */
public abstract class CustomizerTestParent extends ProductTestParent {
    protected Customizer customizer;
    protected AbstractIntegrationBuilder<?> ib;

    public abstract Customizer newCustomizer();

    @BeforeEach
    public void init() {
        ib = dummyIb();
        customizer = newCustomizer();
        customizer.setIntegrationBuilder(ib);
    }
}
