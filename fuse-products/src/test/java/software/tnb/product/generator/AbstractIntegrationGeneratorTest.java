package software.tnb.product.generator;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.product.ProductType;
import software.tnb.product.customizer.Customizers;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.integration.builder.IntegrationBuilder;
import software.tnb.product.parent.TestParent;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractIntegrationGeneratorTest extends TestParent {
    public abstract String process(AbstractIntegrationBuilder<?> ib);

    protected IntegrationBuilder builderWithAdditionalClass() {
        Path classFile = null;
        try {
            classFile = Paths.get(this.getClass().getResource("/software/tnb/product/generator/AddedClass.txt").toURI());
        } catch (Exception e) {
            fail("Unable to load class", e);
        }

        return dummyIb().addClass(classFile);
    }

    @Test
    public void shouldProcessCustomizersTest() {
        setProduct(ProductType.CAMEL_QUARKUS);
        final String key = "hello";
        final String value = "world";

        IntegrationBuilder ib = dummyIb().addCustomizer(Customizers.QUARKUS.customize(i -> i.addToProperties(key, value)));

        process(ib);

        assertThat(ib.getApplicationProperties()).hasSize(1);
        assertThat(ib.getApplicationProperties()).containsKey(key);
        assertThat(ib.getApplicationProperties().get(key)).isEqualTo(value);
    }

    @Test
    public void shouldAddDefaultCustomizersTest() {
        setProduct(ProductType.CAMEL_QUARKUS);

        IntegrationBuilder ib = dummyIb();
        process(ib);

        assertThat(ib.getCustomizers()).isNotEmpty();
    }

    @Test
    public abstract void shouldProcessRouteBuilderTest();

    @Test
    public abstract void shouldProcessAdditionalClassesTest();
}
