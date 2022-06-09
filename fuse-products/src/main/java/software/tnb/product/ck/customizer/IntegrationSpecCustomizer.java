package software.tnb.product.ck.customizer;

import software.tnb.common.utils.MapUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.camelk.v1.IntegrationSpecBuilder;
import io.fabric8.camelk.v1.TraitSpec;

/**
 * Allows customizing Spec inside Integration custom resource.
 */
public interface IntegrationSpecCustomizer {

    void customizeIntegration(IntegrationSpecBuilder integrationSpecBuilder);

    default void mergeTraitConfiguration(IntegrationSpecBuilder integrationSpecBuilder, String trait, Map<String, Object> configuration) {
        ObjectMapper mapper = new ObjectMapper();
        if (integrationSpecBuilder.getTraits() == null) {
            integrationSpecBuilder.withTraits(new HashMap<>());
        }
        if (!integrationSpecBuilder.getTraits().containsKey(trait)) {
            integrationSpecBuilder.getTraits().put(trait, new TraitSpec(mapper.valueToTree(new HashMap<String, Object>())));
        }

        Map<String, Object> config = mapper.convertValue(integrationSpecBuilder.getTraits().get(trait).getConfiguration(),
            new TypeReference<>() { });
        // Merge what was already there with new configuration
        integrationSpecBuilder.getTraits().get(trait).setConfiguration(mapper.valueToTree(MapUtils.merge(config, configuration)));
    }
}
