package org.jboss.fuse.tnb.product.customizer.csb;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.csb.customizer.CamelMainCustomizer;
import org.jboss.fuse.tnb.product.customizer.Customizer;
import org.jboss.fuse.tnb.product.customizer.CustomizerTestParent;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
public class CamelMainCustomizerTest extends CustomizerTestParent {
    @Test
    public void shouldAddToPropertiesTest() {
        customizer.doCustomize();
        assertThat(ib.getProperties()).hasSize(1);
        assertThat(ib.getProperties()).containsEntry("camel.springboot.main-run-controller", "true");
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
