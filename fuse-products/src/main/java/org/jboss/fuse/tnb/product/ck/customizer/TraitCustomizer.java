package org.jboss.fuse.tnb.product.ck.customizer;

import java.util.Map;

import io.fabric8.camelk.v1.IntegrationSpecBuilder;

public class TraitCustomizer extends CamelKCustomizer implements IntegrationSpecCustomizer {
    private final String trait;
    private final Map<String, Object> configuration;

    public TraitCustomizer(String trait, Map<String, Object> configuration) {
        this.trait = trait;
        this.configuration = configuration;
    }

    @Override
    public void customizeIntegration(IntegrationSpecBuilder integrationSpecBuilder) {
        mergeTraitConfiguration(integrationSpecBuilder, trait, configuration);
    }

    @Override
    public void customize() {
        // no-op
    }
}
