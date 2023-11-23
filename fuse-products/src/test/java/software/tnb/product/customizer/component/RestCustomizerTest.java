package software.tnb.product.customizer.component;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.product.ProductType;
import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.component.rest.RestCustomizer;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.apache.camel.v1.IntegrationSpec;
import org.apache.maven.model.Dependency;

import java.util.Map;

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

        IntegrationSpec spec = new IntegrationSpec();
        ((IntegrationSpecCustomizer) customizer).customizeIntegration(spec);

        assertThat(spec.getTraits().getBuilder().getProperties()).isNotNull().hasSize(1).contains("quarkus.camel.servlet.url-patterns=/camel/*");
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
