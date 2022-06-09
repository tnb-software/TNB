package software.tnb.product.openshift.ck;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.product.parent.TestParent;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.product.ck.application.CamelKApp;
import software.tnb.product.ck.customizer.CamelKCustomizer;
import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.ck.integration.builder.CamelKIntegrationBuilder;
import software.tnb.product.ck.integration.resource.ResourceType;
import software.tnb.product.integration.Resource;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.awaitility.Awaitility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.fabric8.camelk.client.CamelKClient;
import io.fabric8.camelk.v1.ConfigurationSpec;
import io.fabric8.camelk.v1.Integration;
import io.fabric8.camelk.v1.IntegrationBuilder;
import io.fabric8.camelk.v1.IntegrationSpecBuilder;
import io.fabric8.camelk.v1.ResourceSpec;
import io.fabric8.camelk.v1.ResourceSpecBuilder;
import io.fabric8.camelk.v1alpha1.KameletBinding;
import io.fabric8.camelk.v1alpha1.KameletBindingBuilder;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.PodBuilder;

@Tag("unit")
public class CamelKAppTest extends CamelKTestParent {
    private Integration createAndGetIntegration(AbstractIntegrationBuilder<?> integrationBuilder) {
        CamelKApp app = new CamelKApp(integrationBuilder);
        executor.submit(app::start);

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(OpenshiftClient.get().adapt(CamelKClient.class).v1().integrations().withName(integrationBuilder.getIntegrationName()).get())
                .isNotNull());
        return OpenshiftClient.get().adapt(CamelKClient.class).v1().integrations().withName(integrationBuilder.getIntegrationName()).get();
    }

    private CamelKIntegrationBuilder ckib() {
        return new CamelKIntegrationBuilder(TestParent.name());
    }

    @Test
    public void shouldCreateIntegrationObjectTest() {
        createAndGetIntegration(dummyIb());
    }

    @Test
    public void shouldContainIntegrationSourceCodeTest() {
        CamelKIntegrationBuilder ib = ckib().fromString("this is the integration code");
        Integration i = createAndGetIntegration(ib);
        assertThat(i.getSpec().getSources()).hasSize(1);
        assertThat(i.getSpec().getSources().get(0).getName()).isEqualTo(ib.getFileName());
        assertThat(i.getSpec().getSources().get(0).getContent()).isEqualTo(ib.getContent());
    }

    @Test
    public void shouldContainYamlIntegrationSourceCodeTest() {
        Path filePath = null;
        try {
            filePath = Paths.get(this.getClass().getResource("/software/tnb/product/openshift/ck/Hello.yaml").toURI());
        } catch (Exception e) {
            fail("Unable to load class", e);
        }

        Integration i = createAndGetIntegration(ckib().fromFile(filePath));
        assertThat(i.getSpec().getFlows()).hasSize(1);
        try {
            assertThat(i.getSpec().getFlows().toString())
                .isEqualTo(new ObjectMapper(new YAMLFactory()).readValue(IOUtils.readFile(filePath), JsonNode.class).toString());
        } catch (JsonProcessingException e) {
            fail("Unable to process yaml string", e);
        }
    }

    @Test
    public void shouldAddDependenciesToIntegrationObjectTest() {
        String[] dependencies = new String[] {"com.test:example", "com.test:model"};

        Integration i = createAndGetIntegration(dummyIb().dependencies(dependencies));
        assertThat(i.getSpec().getDependencies()).contains(Arrays.stream(dependencies).map(d -> "mvn:" + d).toArray(String[]::new));
    }

    @Test
    public void shouldCreateCamelDependenciesTest() {
        Integration i = createAndGetIntegration(ckib().fromString("// camel-k: dependency=camel-quarkus-openapi-java"));
        assertThat(i.getSpec().getDependencies()).hasSize(1);
        assertThat(i.getSpec().getDependencies().get(0)).isEqualTo("camel:openapi-java");
    }

    @Test
    public void shouldAddBuildPropertiesToIntegrationObjectTest() {
        Integration i = createAndGetIntegration(ckib().fromString("// camel-k: jvm=k1=v1 build-property=k2=v2 build-property=k3=v3"));
        assertThat(i.getSpec().getTraits()).containsKey("builder");
        Map<String, Object> config = new ObjectMapper().convertValue(i.getSpec().getTraits().get("builder").getConfiguration(),
            new TypeReference<>() { });
        assertThat(config.get("properties")).isEqualTo(List.of("k2=v2", "k3=v3"));
    }

    @Test
    public void shouldAddTraitsToIntegrationObjectTest() {
        Integration i = createAndGetIntegration(ckib().fromString("// camel-k: trait=prometheus.enabled=true"));
        assertThat(i.getSpec().getTraits()).containsKey("prometheus");
        Map<String, Object> config = new ObjectMapper().convertValue(i.getSpec().getTraits().get("prometheus").getConfiguration(),
            new TypeReference<>() { });
        assertThat(config).isEqualTo(Map.of("enabled", true));
    }

    @Test
    public void shouldProcessPropertiesTest() {
        final String key = "testkey";
        final String value = "testvalue";
        Integration i = createAndGetIntegration(dummyIb().addToProperties(key, value));

        assertThat(i.getSpec().getConfiguration()).isNotNull().isNotEmpty().hasSize(1);
        final ConfigurationSpec config = i.getSpec().getConfiguration().get(0);
        assertThat(config.getType()).isEqualTo("configmap");
        assertThat(config.getValue()).isEqualTo(TestParent.name());

        ConfigMap cm = OpenshiftClient.get().getConfigMap(TestParent.name());
        assertThat(cm).isNotNull();
        assertThat(cm.getData()).containsKey("application.properties");
        assertThat(cm.getData().get("application.properties")).isEqualTo(key + "=" + value);
    }

    @Test
    public void shouldAddGeneralResourcesToIntegrationObjectTest() {
        final String name = "resourceName";
        final String content = "resourceContent";
        Integration i = createAndGetIntegration(dummyIb().addResource(new Resource(name, content)));

        assertThat(i.getSpec().getResources()).isNotNull().hasSize(1);
        ResourceSpec resource = i.getSpec().getResources().get(0);
        assertThat(resource.getName()).isEqualTo(name);
        assertThat(resource.getContent()).isEqualTo(content);
        assertThat(resource.getType()).isEqualTo("data");
        assertThat(resource.getMountPath()).isEqualTo("/etc/camel/resources/" + name);
    }

    @Test
    public void shouldAddCamelKResourcesToIntegrationObjectTest() {
        final String name = "resourceName";
        final String content = "resourceContent";
        Integration i = createAndGetIntegration(ckib().fromString("").addResource(ResourceType.OPENAPI, name, content));

        assertThat(i.getSpec().getResources()).isNotNull().hasSize(1);
        ResourceSpec resource = i.getSpec().getResources().get(0);
        assertThat(resource.getName()).isEqualTo(name);
        assertThat(resource.getContent()).isEqualTo(content);
        assertThat(resource.getType()).isEqualTo("openapi");
        assertThat(resource.getMountPath()).isNull();
    }

    @Test
    public void shouldProcessIntegrationSpecCustomizersTest() {
        Integration i = createAndGetIntegration(dummyIb().addCustomizer(new TestIntegrationSpecCustomizer()));

        assertThat(i.getSpec().getResources()).isNotNull().hasSize(1);
        ResourceSpec resource = i.getSpec().getResources().get(0);
        assertThat(resource.getName()).isEqualTo("integrationspeccustomizer");
    }

    @Test
    public void shouldAddSecretToIntegrationObjectTest() {
        final String secretName = "testsecret";
        Integration i = createAndGetIntegration(ckib().fromString("").secret(secretName));

        assertThat(i.getSpec().getConfiguration()).hasSize(1);
        final ConfigurationSpec config = i.getSpec().getConfiguration().get(0);
        assertThat(config.getType()).isEqualTo("secret");
        assertThat(config.getValue()).isEqualTo(secretName);
    }

    @Test
    public void shouldNotBeReadyWhenIntegrationIsNotReadyTest() {
        CamelKApp app = new CamelKApp(dummyIb());
        createIntegrationObject(false);

        assertThat(app.isReady()).isFalse();
    }

    @Test
    public void shouldNotBeReadyWhenPodIsNotReadyTest() {
        CamelKApp app = new CamelKApp(dummyIb());
        createIntegrationObject(true);
        OpenshiftClient.get().pods().create(new PodBuilder()
            .withNewMetadata()
            .withLabels(Map.of("camel.apache.org/integration", TestParent.name()))
            .withName("camel-k")
            .endMetadata()
            .withNewStatus()
            .addNewContainerStatus()
            .withReady(false)
            .endContainerStatus()
            .endStatus()
            .build()
        );

        assertThat(app.isReady()).isFalse();
    }

    @Test
    public void shouldBeReadyTest() {
        CamelKApp app = new CamelKApp(dummyIb());

        createIntegrationObject(true);

        OpenshiftClient.get().pods().create(new PodBuilder()
            .withNewMetadata()
            .withLabels(Map.of("camel.apache.org/integration", TestParent.name()))
            .withName("camel-k")
            .endMetadata()
            .withNewStatus()
            .addNewContainerStatus()
            .withReady(true)
            .endContainerStatus()
            .endStatus()
            .build()
        );

        assertThat(app.isReady()).isTrue();
    }

    @Test
    public void shouldntBeFailedTest() {
        CamelKApp app = new CamelKApp(dummyIb());
        createIntegrationObject(true);

        assertThat(app.isFailed()).isFalse();
    }

    @Test
    public void shouldBeFailedTest() {
        CamelKApp app = new CamelKApp(dummyIb());
        createIntegrationObject(false);

        assertThat(app.isFailed()).isTrue();
    }

    @Test
    public void shouldCreateKameletBindingTest() {
        CamelKApp app = new CamelKApp(new KameletBindingBuilder().withNewMetadata().withName(TestParent.name()).endMetadata().build());
        executor.submit(app::start);

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> assertThat(kameletBindingClient.withName(TestParent.name()).get()).isNotNull());
    }

    @Test
    public void shouldRemoveKameletBindingTest() {
        KameletBinding kb = new KameletBindingBuilder().withNewMetadata().withName(TestParent.name()).endMetadata().build();
        kameletBindingClient.create(kb);
        CamelKApp app = new CamelKApp(kb);

        app.stop();
        assertThat(OpenshiftClient.get().adapt(CamelKClient.class).v1().integrations().withName(TestParent.name()).get()).isNull();
    }

    @Test
    public void shouldRemoveIntegrationTest() {
        CamelKApp app = new CamelKApp(dummyIb());
        createIntegrationObject(true);

        app.stop();
        assertThat(OpenshiftClient.get().adapt(CamelKClient.class).v1().integrations().withName(TestParent.name()).get()).isNull();
    }

    private void createIntegrationObject(boolean success) {
        OpenshiftClient.get().adapt(CamelKClient.class).v1().integrations().create(new IntegrationBuilder()
            .withNewMetadata()
            .withName(TestParent.name())
            .endMetadata()
            .withNewStatus()
            .withPhase(success ? "running" : "error")
            .endStatus()
            .build()
        );

    }

    private class TestIntegrationSpecCustomizer extends CamelKCustomizer implements IntegrationSpecCustomizer {
        @Override
        public void customizeIntegration(IntegrationSpecBuilder integrationSpecBuilder) {
            integrationSpecBuilder.addToResources(new ResourceSpecBuilder().withName("integrationspeccustomizer").build());
        }

        @Override
        public void customize() { }
    }
}
