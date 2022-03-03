package org.jboss.fuse.tnb.product.customizer.ck;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.fuse.tnb.product.ck.customizer.TraitCustomizer;
import org.jboss.fuse.tnb.product.parent.TestParent;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.camelk.v1.IntegrationSpecBuilder;
import io.fabric8.camelk.v1.TraitSpec;
import io.fabric8.camelk.v1.TraitSpecBuilder;

@Tag("unit")
public class TraitCustomizerTest extends TestParent {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldMergeWithEmptyTraitsTest() {
        IntegrationSpecBuilder builder = new IntegrationSpecBuilder();

        TraitCustomizer c = new TraitCustomizer("builder", Map.of("properties", List.of("key=value")));
        c.customizeIntegration(builder);

        assertThat(builder.getTraits()).hasSize(1);
        assertThat(builder.getTraits()).containsKey("builder");

        Map<String, Object> config = MAPPER.convertValue(builder.getTraits().get("builder").getConfiguration(), new TypeReference<>() { });
        assertThat(config).hasSize(1);
        assertThat(config).containsEntry("properties", List.of("key=value"));
    }

    @Test
    public void shouldMergeWithDifferentTraitTest() {
        TraitSpec spec = new TraitSpecBuilder().withConfiguration(MAPPER.valueToTree(Map.of("config", List.of("k1=v1")))).build();
        Map<String, TraitSpec> traits = new HashMap<>();
        traits.put("jvm", spec);
        IntegrationSpecBuilder builder = new IntegrationSpecBuilder().withTraits(traits);

        TraitCustomizer c = new TraitCustomizer("builder", Map.of("properties", List.of("key=value")));
        c.customizeIntegration(builder);

        assertThat(builder.getTraits()).hasSize(2);
        assertThat(builder.getTraits()).containsKey("builder");
        assertThat(builder.getTraits()).containsKey("jvm");

        Map<String, Object> config = MAPPER.convertValue(builder.getTraits().get("builder").getConfiguration(), new TypeReference<>() { });
        assertThat(config).hasSize(1);
        assertThat(config).containsEntry("properties", List.of("key=value"));

        config = MAPPER.convertValue(builder.getTraits().get("jvm").getConfiguration(), new TypeReference<>() { });
        assertThat(config).hasSize(1);
        assertThat(config).containsEntry("config", List.of("k1=v1"));
    }

    @Test
    public void shouldMergeConfigForTraitTest() {
        TraitSpec spec = new TraitSpecBuilder().withConfiguration(MAPPER.valueToTree(Map.of("properties", List.of("k1=v1")))).build();
        Map<String, TraitSpec> traits = new HashMap<>();
        traits.put("builder", spec);
        IntegrationSpecBuilder builder = new IntegrationSpecBuilder().withTraits(traits);

        TraitCustomizer c = new TraitCustomizer("builder", Map.of("properties", List.of("k2=v2")));
        c.customizeIntegration(builder);

        assertThat(builder.getTraits()).hasSize(1);
        assertThat(builder.getTraits()).containsKey("builder");

        Map<String, Object> config = MAPPER.convertValue(builder.getTraits().get("builder").getConfiguration(), new TypeReference<>() { });
        assertThat(config).hasSize(1);
        assertThat(config).containsEntry("properties", List.of("k1=v1", "k2=v2"));
    }
}
