package org.jboss.fuse.tnb.product.generator;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.customizer.Customizers;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.builder.IntegrationBuilder;
import org.jboss.fuse.tnb.product.parent.TestParent;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractIntegrationGeneratorTest extends TestParent {
    public abstract String process(AbstractIntegrationBuilder<?> ib);

    protected IntegrationBuilder builderWithAdditionalClass() {
        Path classFile = null;
        try {
            classFile = Paths.get(this.getClass().getResource("/org/jboss/fuse/tnb/product/generator/AddedClass.txt").toURI());
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

        assertThat(ib.getProperties()).hasSize(1);
        assertThat(ib.getProperties()).containsKey(key);
        assertThat(ib.getProperties().get(key)).isEqualTo(value);
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
