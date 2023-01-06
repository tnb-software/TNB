package software.tnb.product.customizer.component;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.component.datasource.DataSourceCustomizer;

import org.junit.jupiter.api.Tag;

import org.assertj.core.api.Assertions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import io.fabric8.camelk.v1.IntegrationSpecBuilder;

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
        Assertions.assertThat(ib.getProperties()).isEqualTo(Map.of(
            "quarkus.datasource.db-kind", type,
            "quarkus.datasource.jdbc.url", url + ";encrypt=false;",
            "quarkus.datasource.username", username,
            "quarkus.datasource.password", password
        ));
        Assertions.assertThat(ib.getDependencies()).hasSize(1);
        Assertions.assertThat(ib.getDependencies().get(0).getGroupId()).isEqualTo("io.quarkus");
        Assertions.assertThat(ib.getDependencies().get(0).getArtifactId()).isEqualTo("quarkus-jdbc-" + type);
    }

    @Override
    public void validateCamelK() {
        validateQuarkus();

        IntegrationSpecBuilder builder = new IntegrationSpecBuilder();
        ((IntegrationSpecCustomizer) customizer).customizeIntegration(builder);

        assertThat(builder.getTraits()).hasSize(1);
        assertThat(builder.getTraits()).containsKey("builder");
        Map<String, Object> cfg = new ObjectMapper().convertValue(builder.getTraits().get("builder").getConfiguration(), new TypeReference<>() {
        });
        assertThat(cfg).hasSize(1);
        assertThat(cfg).containsEntry("properties", List.of("quarkus.datasource.db-kind=" + type));
    }

    @Override
    public void validateSpringBoot() {
        customizer.doCustomize();
        Assertions.assertThat(ib.getProperties()).isEqualTo(Map.of(
            "spring.datasource.url", url,
            "spring.datasource.username", username,
            "spring.datasource.password", password,
            "spring.datasource.driver-class-name", driver
        ));

        Assertions.assertThat(ib.getDependencies()).hasSize(1);
        Assertions.assertThat(ib.getDependencies().get(0).getGroupId()).isEqualTo("org.springframework.boot");
        Assertions.assertThat(ib.getDependencies().get(0).getArtifactId()).isEqualTo("spring-boot-starter-jdbc");
    }

    @Override
    public Customizer newCustomizer() {
        return new DataSourceCustomizer(type, url, username, password, driver);
    }
}
