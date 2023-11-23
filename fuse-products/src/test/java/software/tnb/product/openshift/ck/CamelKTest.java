package software.tnb.product.openshift.ck;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.product.ProductFactory;
import software.tnb.product.ck.CamelK;
import software.tnb.product.ck.configuration.CamelKConfiguration;
import software.tnb.product.ck.configuration.CamelKProdConfiguration;
import software.tnb.product.ck.configuration.CamelKUpstreamConfiguration;
import software.tnb.product.openshift.OpenshiftTestParent;
import software.tnb.product.parent.TestParent;
import software.tnb.util.openshift.TestOpenshiftClient;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.apache.camel.v1.Integration;
import org.apache.camel.v1.IntegrationPlatform;
import org.apache.camel.v1.Kamelet;
import org.apache.camel.v1.KameletStatus;
import org.apache.camel.v1.integrationplatformspec.build.maven.settings.ConfigMapKeyRef;
import org.apache.camel.v1alpha1.KameletBinding;
import org.awaitility.Awaitility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.fabric8.kubernetes.api.model.DefaultKubernetesResourceList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.server.mock.OutputStreamMessage;
import io.fabric8.kubernetes.client.utils.Utils;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.CatalogSourceBuilder;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.InstallPlanBuilder;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.InstallPlanStatusBuilder;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.Subscription;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.SubscriptionBuilder;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.SubscriptionStatusBuilder;

@Tag("unit")
public class CamelKTest extends CamelKTestParent {
    private final CamelKConfiguration config = CamelKConfiguration.getConfiguration();
    private CamelK camelK;

    @BeforeEach
    public void createCamelK() {
        camelK = new CamelK();
    }

    @AfterEach
    public void clearProperties() {
        List.of(TestConfiguration.MAVEN_SETTINGS, TestConfiguration.MAVEN_REPOSITORY, CamelKConfiguration.FORCE_UPSTREAM)
            .forEach(System::clearProperty);
    }

    private void deploy() {
        OpenshiftClient.get().operatorHub().catalogSources().inNamespace(config.subscriptionSourceNamespace()).createOrReplace(
            new CatalogSourceBuilder()
                .withNewMetadata()
                .withName(config.subscriptionSource())
                .endMetadata()
                .build()
        );

        executor.submit(() -> new CamelK().setupProduct());
    }

    private void deployAndWait() {
        deploy();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(OpenshiftClient.get().operatorHub().subscriptions()
            .withName(config.subscriptionName()).get()).isNotNull());
        OpenshiftClient.get().operatorHub().subscriptions().withName(config.subscriptionName()).edit(
            s -> new SubscriptionBuilder(s).withStatus(
                new SubscriptionStatusBuilder().withNewInstallplan().withName("ip").endInstallplan().build()).build()
        );

        OpenshiftClient.get().operatorHub().installPlans().create(
            new InstallPlanBuilder()
                .withNewMetadata()
                .withName("ip")
                .endMetadata()
                .withStatus(new InstallPlanStatusBuilder().withPhase("complete").build()).build()
        );

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(OpenshiftClient.get().resources(IntegrationPlatform.class).withName(config.integrationPlatformName()).get()).isNotNull());
    }

    @Test
    public void shouldFailWhenCatalogSourceDoesntExistTest() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> ProductFactory.create().setupProduct());
    }

    @Test
    public void shouldCreateOperatorHubResourcesTest() {
        deploy();
        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
            assertThat(OpenshiftClient.get().operatorHub().operatorGroups().withName(config.subscriptionName()).get()).isNotNull());
        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
            assertThat(OpenshiftClient.get().operatorHub().subscriptions().withName(config.subscriptionName()).get()).isNotNull());
    }

    @Test
    public void shouldSetBuildTimeoutTest() {
        deployAndWait();

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(OpenshiftClient.get().resources(IntegrationPlatform.class).withName(config.integrationPlatformName()).get()).isNotNull());
        IntegrationPlatform ip = OpenshiftClient.get().resources(IntegrationPlatform.class).withName(config.integrationPlatformName()).get();

        assertThat(ip.getSpec().getBuild().getTimeout()).isNotNull();
        assertThat(ip.getSpec().getBuild().getTimeout()).isEqualTo(config.mavenBuildTimeout() + "m");
    }

    @Test
    public void shouldCreateIntegrationPlatformWithMavenSettingsTest() {
        final String settingsContent;

        try {
            Path settings = Paths.get(this.getClass().getResource("/software/tnb/product/openshift/ck/settings.xml").toURI());
            settingsContent = IOUtils.readFile(settings);
            System.setProperty(TestConfiguration.MAVEN_SETTINGS, settings.toAbsolutePath().toString());

            deployAndWait();

            IntegrationPlatform ip = OpenshiftClient.get().resources(IntegrationPlatform.class).withName(config.integrationPlatformName()).get();
            final ConfigMapKeyRef configMapKeyRef = ip.getSpec().getBuild().getMaven().getSettings().getConfigMapKeyRef();
            assertThat(configMapKeyRef).isNotNull();
            assertThat(configMapKeyRef.getKey()).isEqualTo("settings.xml");
            assertThat(configMapKeyRef.getName()).isEqualTo(config.mavenSettingsConfigMapName());

            assertThat(OpenshiftClient.get().getConfigMap(config.mavenSettingsConfigMapName())).isNotNull();
            assertThat(OpenshiftClient.get().getConfigMap(config.mavenSettingsConfigMapName()).getData())
                .isEqualTo(Map.of("settings.xml", settingsContent));
        } catch (Exception e) {
            fail("Unable to load settings file", e);
        }
    }

    @Test
    public void shouldCreateIntegrationPlatformWithMavenRepositoryTest() {
        System.setProperty(TestConfiguration.MAVEN_REPOSITORY, "my-repo");

        deployAndWait();
        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(OpenshiftClient.get().resources(IntegrationPlatform.class).withName(config.integrationPlatformName()).get()).isNotNull());
        assertThat(OpenshiftClient.get().getConfigMap(config.mavenSettingsConfigMapName())).isNotNull();
        assertThat(OpenshiftClient.get().getConfigMap(config.mavenSettingsConfigMapName()).getData().get("settings.xml")).contains("my-repo");
    }

    @Test
    public void isReadyShouldReturnFalseWhenPodIsntReadyTest() {
        Pod operator = operatorPod(false);
        operator.getStatus().getContainerStatuses().get(0).setReady(false);
        OpenshiftClient.get().pods().resource(operator).create();

        assertThat(ProductFactory.create(CamelK.class).isReady()).isFalse();
    }

    @Test
    public void isReadyShouldReturnFalseWhenKameletsAreNotDeployedTest() {
        TestOpenshiftClient.setServer(OpenshiftTestParent.expectServer);

        expectOperatorGet();
        expectKameletLs();
        expectKamelets(null);
        expectIntegrationPlatform();

        assertThat(camelK.isReady()).isFalse();
    }

    @Test
    public void kameletsReadyShouldReturnFalseWhenKameletIsntReadyTest() {
        TestOpenshiftClient.setServer(OpenshiftTestParent.expectServer);

        expectOperatorGet();
        expectKameletLs();
        expectKamelets(false);
        expectIntegrationPlatform();

        assertThat(camelK.isReady()).isFalse();
    }

    @Test
    public void shouldBeReadyTest() {
        TestOpenshiftClient.setServer(OpenshiftTestParent.expectServer);

        expectOperatorGet();
        expectKameletLs();
        expectKamelets(true);
        expectIntegrationPlatform();

        assertThat(camelK.isReady()).isTrue();
    }

    @Test
    public void shouldCreateAppFromIntegrationBuilderTest() {
        deployAndWait();

        executor.submit(() -> camelK.createIntegration(dummyIb()));

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> assertThat(OpenshiftClient.get().resources(Integration.class)
            .withName(TestParent.name())).isNotNull());
    }

    @Test
    public void shouldCreateAppFromKameletBindingTest() {
        deployAndWait();

        executor.submit(() -> {
            ObjectMeta metadata = new ObjectMetaBuilder().withName("kb").build();
            KameletBinding kb = new KameletBinding();
            kb.setMetadata(metadata);
            camelK.createKameletBinding(kb);
        });
        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(kameletBindingClient.withName("kb").get()).isNotNull());
    }

    @Test
    public void shouldDeleteSubscriptionTest() {
        OpenshiftClient.get().pods().create(operatorPod(true));
        OpenshiftClient.get().operatorHub().subscriptions().create(new SubscriptionBuilder()
            .withNewMetadata()
            .withName(config.subscriptionName())
            .endMetadata()
            .withNewStatus()
            .withCurrentCSV("test")
            .endStatus()
            .build()
        );
        ProductFactory.create().teardownProduct();
        Awaitility.await().atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> assertThat(OpenshiftClient.get().operatorHub().subscriptions().withName(config.subscriptionName()).get()).isNull());
    }

    @Test
    public void shouldDeployProdByDefaultTest() {
        deploy();

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
            assertThat(OpenshiftClient.get().operatorHub().subscriptions().withName(config.subscriptionName()).get()).isNotNull());

        Subscription s = OpenshiftClient.get().operatorHub().subscriptions().withName(config.subscriptionName()).get();
        assertThat(s.getSpec().getChannel()).isEqualTo(CamelKProdConfiguration.getConfiguration().subscriptionChannel());
        assertThat(s.getSpec().getName()).isEqualTo(CamelKProdConfiguration.getConfiguration().subscriptionOperatorName());
        assertThat(s.getSpec().getSource()).isEqualTo(CamelKProdConfiguration.getConfiguration().subscriptionSource());
        assertThat(s.getSpec().getSourceNamespace()).isEqualTo(CamelKProdConfiguration.getConfiguration().subscriptionSourceNamespace());
    }

    @Test
    public void shouldDeployUpstreamTest() {
        System.setProperty(CamelKConfiguration.FORCE_UPSTREAM, "true");
        CamelKConfiguration upstreamConfiguration = CamelKUpstreamConfiguration.getConfiguration();

        OpenshiftClient.get().operatorHub().catalogSources().inNamespace(upstreamConfiguration.subscriptionSourceNamespace()).createOrReplace(
            new CatalogSourceBuilder()
                .withNewMetadata()
                .withName(upstreamConfiguration.subscriptionSource())
                .endMetadata()
                .build()
        );

        executor.submit(() -> camelK.setupProduct());

        Awaitility.await().atMost(Duration.ofSeconds(10))
            .until(() -> OpenshiftClient.get().operatorHub().subscriptions().withName(config.subscriptionName()).get() != null);

        Subscription s = OpenshiftClient.get().operatorHub().subscriptions().withName(config.subscriptionName()).get();
        assertThat(s.getSpec().getChannel()).isEqualTo(upstreamConfiguration.subscriptionChannel());
        assertThat(s.getSpec().getName()).isEqualTo(upstreamConfiguration.subscriptionOperatorName());
        assertThat(s.getSpec().getSource()).isEqualTo(upstreamConfiguration.subscriptionSource());
        assertThat(s.getSpec().getSourceNamespace()).isEqualTo(upstreamConfiguration.subscriptionSourceNamespace());
    }

    private void expectKameletLs() {
        final String cmd = "ls /kamelets/* | wc -l";
        OpenshiftTestParent.expectServer.expect().get().withPath("/api/v1/namespaces/test/pods/camel-k/exec?command=bash&command=-c&command="
                + Utils.toUrlEncoded(cmd).replace("+", "%20") + "&stdout=true&stderr=true")
            .andUpgradeToWebSocket()
            .open(new OutputStreamMessage("1"))
            .done().always();
    }

    private void expectKamelets(Object config) {
        final DefaultKubernetesResourceList<Kamelet> list = new DefaultKubernetesResourceList<>();
        if (config != null) {
            String phase = Boolean.parseBoolean(config.toString()) ? "Ready" : "Error";
            Kamelet k = new Kamelet();

            ObjectMeta metadata = new ObjectMetaBuilder().withName(phase).build();
            k.setMetadata(metadata);

            KameletStatus status = new KameletStatus();
            status.setPhase(phase);
            k.setStatus(status);

            list.setItems(List.of(k));
        }
        OpenshiftTestParent.expectServer.expect().get().withPath("/apis/camel.apache.org/v1/namespaces/test/kamelets")
            .andReturn(200, list).always();
    }
}
