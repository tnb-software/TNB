package org.jboss.fuse.tnb.product.customizer.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.customizer.Customizer;
import org.jboss.fuse.tnb.product.customizer.Customizers;
import org.jboss.fuse.tnb.product.customizer.ProductsCustomizer;
import org.jboss.fuse.tnb.product.integration.builder.IntegrationBuilder;
import org.jboss.fuse.tnb.product.parent.TestParent;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;

@Tag("unit")
public class CustomizerTest extends TestParent {
    @ParameterizedTest
    @EnumSource(Customizers.class)
    public void shouldNotRunForDifferentProductTest(Customizers cs) {
        switch (cs) {
            case CAMELK:
            case SPRINGBOOT:
                setProduct(ProductType.CAMEL_QUARKUS);
                break;
            case QUARKUS:
                setProduct(ProductType.CAMEL_K);
                break;
            default:
                throw new IllegalArgumentException("Implement new switch case for " + cs.name());
        }

        IntegrationBuilder ib = dummyIb();
        Customizer c = cs.customize(i -> i.addToProperties("key", "value"));
        c.setIntegrationBuilder(ib);
        c.doCustomize();
        assertThat(ib.getProperties()).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(Customizers.class)
    public void shouldRunForGivenProduct(Customizers cs) {
        switch (cs) {
            case CAMELK:
                setProduct(ProductType.CAMEL_K);
                break;
            case SPRINGBOOT:
                setProduct(ProductType.CAMEL_SPRINGBOOT);
                break;
            case QUARKUS:
                setProduct(ProductType.CAMEL_QUARKUS);
                break;
            default:
                throw new IllegalArgumentException("Implement new switch case for " + cs.name());
        }

        IntegrationBuilder ib = dummyIb();
        Customizer c = cs.customize(i -> i.addToProperties("key", "value"));
        c.setIntegrationBuilder(ib);
        c.doCustomize();
        assertThat(ib.getProperties()).isEqualTo(Map.of("key", "value"));
    }

    @ParameterizedTest
    @EnumSource(ProductType.class)
    public void shouldRunForAllProducts(ProductType type) {
        setProduct(type);
        Customizer c = new ProductsCustomizer() {
            @Override
            public void customizeCamelK() {
                getIntegrationBuilder().addToProperties("key", type.name());
            }

            @Override
            public void customizeQuarkus() {
                getIntegrationBuilder().addToProperties("key", type.name());
            }

            @Override
            public void customizeSpringboot() {
                getIntegrationBuilder().addToProperties("key", type.name());
            }
        };
        IntegrationBuilder ib = dummyIb();
        c.setIntegrationBuilder(ib);
        c.doCustomize();

        assertThat(ib.getProperties()).isEqualTo(Map.of("key", type.name()));
    }
}
