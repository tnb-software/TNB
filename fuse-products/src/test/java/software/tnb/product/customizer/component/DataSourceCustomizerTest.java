package software.tnb.product.customizer.component;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.component.datasource.DataSourceCustomizer;

import org.junit.jupiter.api.Tag;

import org.apache.camel.v1.IntegrationSpec;

import java.util.Map;

@Tag("unit")
public class DataSourceCustomizerTest extends ProductCustomizerTestParent {
    private final String type = "type";
    private final String url = "url";
    private final String username = "username";
    private final String password = "password";
    private final String driver = "driver";

    @Override
    public void validateQuarkus() {
        customizer.doCustomize();
        assertThat(ib.getProperties()).isEqualTo(Map.of(
            "quarkus.datasource.db-kind", type,
            "quarkus.datasource.jdbc.url", url,
            "quarkus.datasource.username", username,
            "quarkus.datasource.password", password
        ));
        assertThat(ib.getDependencies()).hasSize(1);
        assertThat(ib.getDependencies().get(0).getGroupId()).isEqualTo("io.quarkus");
        assertThat(ib.getDependencies().get(0).getArtifactId()).isEqualTo("quarkus-jdbc-" + type);
    }

    @Override
    public void validateCamelK() {
        validateQuarkus();

        IntegrationSpec spec = new IntegrationSpec();
        ((IntegrationSpecCustomizer) customizer).customizeIntegration(spec);

        assertThat(spec.getTraits().getBuilder().getProperties()).isNotNull().hasSize(1).contains("quarkus.datasource.db-kind=" + type);
    }

    @Override
    public void validateSpringBoot() {
        customizer.doCustomize();
        assertThat(ib.getProperties()).isEqualTo(Map.of(
            "spring.datasource.url", url,
            "spring.datasource.username", username,
            "spring.datasource.password", password,
            "spring.datasource.driver-class-name", driver
        ));

        assertThat(ib.getDependencies()).hasSize(1);
        assertThat(ib.getDependencies().get(0).getGroupId()).isEqualTo("org.springframework.boot");
        assertThat(ib.getDependencies().get(0).getArtifactId()).isEqualTo("spring-boot-starter-jdbc");
    }

    @Override
    public Customizer newCustomizer() {
        return new DataSourceCustomizer(type, url, username, password, driver);
    }
}
