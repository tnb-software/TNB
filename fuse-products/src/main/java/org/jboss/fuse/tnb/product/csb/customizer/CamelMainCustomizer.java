package org.jboss.fuse.tnb.product.csb.customizer;

public class CamelMainCustomizer extends SpringbootCustomizer {
    @Override
    public void customize() {
        getIntegrationBuilder().addToProperties("camel.springboot.main-run-controller", "true");
    }
}
