package software.tnb.product.openshift.ck;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.product.ck.application.CamelKApp;
import software.tnb.product.ck.customizer.CamelKCustomizer;
import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.ck.integration.builder.CamelKIntegrationBuilder;
import software.tnb.product.ck.integration.resource.ResourceType;
import software.tnb.product.integration.Resource;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.parent.TestParent;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.apache.camel.v1.Integration;
import org.apache.camel.v1.IntegrationSpec;
import org.apache.camel.v1.IntegrationStatus;
import org.apache.camel.v1.integrationspec.Configuration;
import org.apache.camel.v1alpha1.KameletBinding;
import org.awaitility.Awaitility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PodBuilder;

@Tag("unit")
public class CamelKAppTest extends CamelKTestParent {
    private Integration createAndGetIntegration(AbstractIntegrationBuilder<?> integrationBuilder) {
        CamelKApp app = new CamelKApp(integrationBuilder);
        executor.submit(app::start);

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(OpenshiftClient.get().resources(Integration.class).withName(integrationBuilder.getIntegrationName()).get())
                .isNotNull());
        return OpenshiftClient.get().resources(Integration.class).withName(integrationBuilder.getIntegrationName()).get();
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
        assertThat(i.getSpec().getTraits().getBuilder()).isNotNull();
        assertThat(i.getSpec().getTraits().getBuilder().getProperties()).isEqualTo(List.of("k2=v2", "k3=v3"));
    }

    @Test
    public void shouldAddTraitsToIntegrationObjectTest() {
        Integration i = createAndGetIntegration(ckib().fromString("// camel-k: trait=prometheus.enabled=true"));
        assertThat(i.getSpec().getTraits().getPrometheus()).isNotNull();
        assertThat(i.getSpec().getTraits().getPrometheus().getEnabled()).isTrue();
    }

    @Test
    public void shouldProcessPropertiesTest() {
        final String key = "testkey";
        final String value = "testvalue";
        Integration i = createAndGetIntegration(dummyIb().addToProperties(key, value));

        assertThat(i.getSpec().getConfiguration()).isNotNull().isNotEmpty().hasSize(1);
        final Configuration config = i.getSpec().getConfiguration().get(0);
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

        assertThat(i.getSpec().getTraits().getMount().getResources()).isNotNull().hasSize(1).contains("configmap:resourceName");
        ConfigMap cm = OpenshiftClient.get().getConfigMap(name);
        assertThat(cm).isNotNull();
        assertThat(cm.getData()).containsKey(name);
        assertThat(cm.getData().get(name)).isEqualTo(content);
    }

    @Test
    public void shouldAddCamelKResourcesToIntegrationObjectTest() {
        final String name = "resourceName";
        final String content = "resourceContent";
        Integration i = createAndGetIntegration(ckib().fromString("").addResource(name, content));

        assertThat(i.getSpec().getTraits().getMount().getResources()).isNotNull().hasSize(1).contains("configmap:resourceName");
        ConfigMap cm = OpenshiftClient.get().getConfigMap(name);
        assertThat(cm).isNotNull();
        assertThat(cm.getData()).containsKey(name);
        assertThat(cm.getData().get(name)).isEqualTo(content);
    }

    @Test
    public void shouldMountConfigMapToIntegrationTest() {
        final String name = "resourceName";
        Integration i = createAndGetIntegration(ckib().fromString("").addResource(ResourceType.CONFIG_MAP, name));
        assertThat(i.getSpec().getTraits().getMount().getResources()).isNotNull().hasSize(1).contains("configmap:resourceName");
    }

    @Test
    public void shouldMountSecretToIntegrationTest() {
        final String name = "resourceName";
        Integration i = createAndGetIntegration(ckib().fromString("").addResource(ResourceType.SECRET, name));
        assertThat(i.getSpec().getTraits().getMount().getResources()).isNotNull().hasSize(1).contains("secret:resourceName");
    }

    @Test
    public void shouldThrowExceptionForFileResourceWithoutContentTest() {
        assertThrows(IllegalArgumentException.class, () -> createAndGetIntegration(ckib().fromString("").addResource(ResourceType.FILE, "test")));
    }

    @Test
    public void shouldProcessIntegrationSpecCustomizersTest() {
        Integration i = createAndGetIntegration(dummyIb().addCustomizer(new TestIntegrationSpecCustomizer()));

        assertThat(i.getSpec().getDependencies()).isNotNull().hasSize(1).contains("testdep");
    }

    @Test
    public void shouldAddSecretToIntegrationObjectTest() {
        final String secretName = "testsecret";
        Integration i = createAndGetIntegration(ckib().fromString("").secret(secretName));

        assertThat(i.getSpec().getConfiguration()).hasSize(1);
        final Configuration config = i.getSpec().getConfiguration().get(0);
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
        KameletBinding kb = new KameletBinding();
        ObjectMeta metadata = new ObjectMetaBuilder().withName(TestParent.name()).build();
        kb.setMetadata(metadata);
        CamelKApp app = new CamelKApp(kb);
        executor.submit(app::start);

        Awaitility.await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> assertThat(kameletBindingClient.withName(TestParent.name()).get()).isNotNull());
    }

    @Test
    public void shouldRemoveKameletBindingTest() {
        KameletBinding kb = new KameletBinding();
        ObjectMeta metadata = new ObjectMetaBuilder().withName(TestParent.name()).build();
        kb.setMetadata(metadata);
        kameletBindingClient.resource(kb).create();
        CamelKApp app = new CamelKApp(kb);

        app.stop();
        assertThat(OpenshiftClient.get().resources(Integration.class).withName(TestParent.name()).get()).isNull();
    }

    @Test
    public void shouldRemoveIntegrationTest() {
        CamelKApp app = new CamelKApp(dummyIb());
        createIntegrationObject(true);

        app.stop();
        assertThat(OpenshiftClient.get().resources(Integration.class).withName(TestParent.name()).get()).isNull();
    }

    private void createIntegrationObject(boolean success) {
        Integration i = new Integration();
        ObjectMeta metadata = new ObjectMetaBuilder().withName(TestParent.name()).build();
        i.setMetadata(metadata);
        IntegrationStatus status = new IntegrationStatus();
        status.setPhase(success ? "running" : "error");
        i.setStatus(status);
        OpenshiftClient.get().resources(Integration.class).resource(i).create();
    }

    private class TestIntegrationSpecCustomizer extends CamelKCustomizer implements IntegrationSpecCustomizer {
        @Override
        public void customizeIntegration(IntegrationSpec integrationSpec) {
            integrationSpec.setDependencies(List.of("testdep"));
        }

        @Override
        public void customize() { }
    }
}
