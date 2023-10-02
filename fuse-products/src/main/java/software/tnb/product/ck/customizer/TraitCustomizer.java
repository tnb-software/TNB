package software.tnb.product.ck.customizer;

import org.apache.camel.v1.IntegrationSpec;
import org.apache.camel.v1.integrationspec.Traits;

import java.util.function.Consumer;

public class TraitCustomizer extends CamelKCustomizer implements IntegrationSpecCustomizer {
    private final Consumer<Traits> config;

    public TraitCustomizer(Consumer<Traits> config) {
        this.config = config;
    }

    @Override
    public void customizeIntegration(IntegrationSpec integrationSpec) {
        Traits traits = integrationSpec.getTraits() == null ? new Traits() : integrationSpec.getTraits();
        config.accept(traits);
        integrationSpec.setTraits(traits);
    }

    @Override
    public void customize() {
        // no-op
    }
}
