package software.tnb.product.customizer.csb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import software.tnb.common.product.ProductType;
import software.tnb.product.csb.customizer.ComponentCustomizer;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.CustomizerTestParent;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.integration.builder.IntegrationBuilder;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import software.tnb.product.parent.TestParent;

@Tag("unit")
public class ComponentCustomizerTest extends CustomizerTestParent {
    @Test
    public void shouldAddComponentAnnotationTest() {
        customizer.doCustomize();
        assertThat(ib.getRouteBuilder().get().getClassByName(AbstractIntegrationBuilder.ROUTE_BUILDER_NAME).get().getAnnotations())
            .anyMatch(a -> "Component".equals(a.getName().asString()));
    }

    @Test
    public void shouldNotRunWithoutRouteBuilderTest() {
        TestParent.setProduct(ProductType.CAMEL_SPRINGBOOT);
        customizer.setIntegrationBuilder(new IntegrationBuilder(""));
        assertThatNoException().isThrownBy(() -> customizer.doCustomize());
    }

    @Override
    public Customizer newCustomizer() {
        return new ComponentCustomizer();
    }

    @Override
    public ProductType product() {
        return ProductType.CAMEL_SPRINGBOOT;
    }
}
