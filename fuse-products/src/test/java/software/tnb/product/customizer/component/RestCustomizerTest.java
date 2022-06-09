package software.tnb.product.customizer.component;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.product.ProductType;
import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.component.rest.RestCustomizer;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.apache.maven.model.Dependency;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import io.fabric8.camelk.v1.IntegrationSpecBuilder;

@Tag("unit")
public class RestCustomizerTest extends ProductCustomizerTestParent {
    @Override
    public void validateQuarkus() {
        customizer.doCustomize();

        assertThat(ib.getProperties())
            .isEqualTo(Map.of("quarkus.camel.servlet.url-patterns", "/camel/*", "quarkus.openshift.route.expose", "true"));
        assertThat(ib.getDependencies()).hasSize(1);
        assertThat(ib.getDependencies().get(0).getArtifactId()).contains("rest");
    }

    @Override
    public void validateCamelK() {
        customizer.doCustomize();

        assertThat(ib.getProperties())
            .isEqualTo(Map.of("quarkus.camel.servlet.url-patterns", "/camel/*", "quarkus.openshift.route.expose", "true"));
        assertThat(ib.getDependencies()).hasSize(1);
        assertThat(ib.getDependencies().get(0).getArtifactId()).contains("rest");

        IntegrationSpecBuilder builder = new IntegrationSpecBuilder();
        ((IntegrationSpecCustomizer) customizer).customizeIntegration(builder);

        assertThat(builder.getTraits()).hasSize(1);
        assertThat(builder.getTraits()).containsKey("builder");
        Map<String, Object> cfg = new ObjectMapper().convertValue(builder.getTraits().get("builder").getConfiguration(), new TypeReference<>() { });
        assertThat(cfg).hasSize(1);
        assertThat(cfg).containsEntry("properties", List.of("quarkus.camel.servlet.url-patterns=/camel/*"));
    }

    @Override
    public void validateSpringBoot() {
        customizer.doCustomize();

        assertThat(ib.getDependencies()).hasSize(2);
        Dependency d = ib.getDependencies().get(0);
        assertThat(d.getGroupId()).isEqualTo("org.springframework.boot");
        assertThat(d.getArtifactId()).isEqualTo("spring-boot-starter-web");
        assertThat(d.getExclusions()).hasSize(1);
        assertThat(d.getExclusions().get(0).getArtifactId()).isEqualTo("spring-boot-starter-tomcat");

        d = ib.getDependencies().get(1);
        assertThat(d.getGroupId()).isEqualTo("org.springframework.boot");
        assertThat(d.getArtifactId()).isEqualTo("spring-boot-starter-undertow");
    }

    @Test
    public void shouldSkipForOpenshiftSpringBootTest() {
        setProduct(ProductType.CAMEL_SPRINGBOOT);
        System.setProperty(OpenshiftConfiguration.USE_OPENSHIFT, "true");
        try {
            customizer.doCustomize();

            assertThat(ib.getDependencies()).isEmpty();
        } finally {
            System.clearProperty(OpenshiftConfiguration.USE_OPENSHIFT);
        }
    }

    @Override
    public Customizer newCustomizer() {
        return new RestCustomizer();
    }
}
