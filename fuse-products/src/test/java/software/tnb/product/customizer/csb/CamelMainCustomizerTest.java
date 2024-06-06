package software.tnb.product.customizer.csb;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.product.ProductType;
import software.tnb.product.csb.customizer.CamelMainCustomizer;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.CustomizerTestParent;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
public class CamelMainCustomizerTest extends CustomizerTestParent {
    @Test
    public void shouldAddToPropertiesTest() {
        customizer.doCustomize();
        assertThat(ib.getApplicationProperties()).hasSize(1);
        assertThat(ib.getApplicationProperties()).containsEntry("camel.springboot.main-run-controller", "true");
    }

    @Override
    public Customizer newCustomizer() {
        return new CamelMainCustomizer();
    }

    @Override
    public ProductType product() {
        return ProductType.CAMEL_SPRINGBOOT;
    }
}
