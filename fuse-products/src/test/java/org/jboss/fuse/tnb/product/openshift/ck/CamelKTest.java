package org.jboss.fuse.tnb.product.openshift.ck;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.product.ProductFactory;
import org.jboss.fuse.tnb.product.ck.CamelK;
import org.jboss.fuse.tnb.product.ck.configuration.CamelKConfiguration;
import org.jboss.fuse.tnb.product.ck.configuration.CamelKProdConfiguration;
import org.jboss.fuse.tnb.product.ck.configuration.CamelKUpstreamConfiguration;
import org.jboss.fuse.tnb.util.ck.TestCamelK;
import org.jboss.fuse.tnb.util.openshift.TestOpenshiftClient;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.awaitility.Awaitility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.fabric8.camelk.client.CamelKClient;
import io.fabric8.camelk.v1.IntegrationPlatform;
import io.fabric8.camelk.v1alpha1.KameletBindingBuilder;
import io.fabric8.camelk.v1alpha1.KameletBuilder;
import io.fabric8.camelk.v1alpha1.KameletListBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapKeySelector;
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
    private TestCamelK camelK;

    @BeforeEach
    public void createCamelK() {
        camelK = new TestCamelK();
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

        executor.submit(() -> camelK.setupProduct());
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
            assertThat(OpenshiftClient.get().adapt(CamelKClient.class).v1().integrationPlatforms().withName(config.integrationPlatformName()).get())
                .isNotNull());
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
            assertThat(OpenshiftClient.get().adapt(CamelKClient.class).v1().integrationPlatforms().withName(config.integrationPlatformName()).get())
                .isNotNull());
        IntegrationPlatform ip =
            OpenshiftClient.get().adapt(CamelKClient.class).v1().integrationPlatforms().withName(config.integrationPlatformName())
                .get();

        assertThat(ip.getSpec().getBuild().getTimeout()).isNotNull();
        assertThat(ip.getSpec().getBuild().getTimeout()).isEqualTo(config.mavenBuildTimeout());
    }

    @Test
    public void shouldCreateIntegrationPlatformWithMavenSettingsTest() {
        final String settingsContent;

        try {
            Path settings = Paths.get(this.getClass().getResource("/org/jboss/fuse/tnb/product/openshift/ck/settings.xml").toURI());
            settingsContent = IOUtils.readFile(settings);
            System.setProperty(TestConfiguration.MAVEN_SETTINGS, settings.toAbsolutePath().toString());

            deployAndWait();

            IntegrationPlatform ip = OpenshiftClient.get().adapt(CamelKClient.class).v1().integrationPlatforms()
                .withName(config.integrationPlatformName()).get();
            assertThat(ip.getSpec().getBuild().getMaven().getSettings().getConfigMapKeyRef()).isNotNull();
            final ConfigMapKeySelector configMapKeyRef = ip.getSpec().getBuild().getMaven().getSettings().getConfigMapKeyRef();
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
            assertThat(OpenshiftClient.get().adapt(CamelKClient.class).v1().integrationPlatforms()
                .withName(config.integrationPlatformName()).get()).isNotNull());
        assertThat(OpenshiftClient.get().getConfigMap(config.mavenSettingsConfigMapName())).isNotNull();
        assertThat(OpenshiftClient.get().getConfigMap(config.mavenSettingsConfigMapName()).getData().get("settings.xml")).contains("my-repo");
    }

    @Test
    public void isReadyShouldReturnFalseWhenPodIsntReadyTest() {
        Pod operator = operatorPod(false);
        operator.getStatus().getContainerStatuses().get(0).setReady(false);
        OpenshiftClient.get().pods().create(operator);

        assertThat(ProductFactory.create(CamelK.class).isReady()).isFalse();
    }

    @Test
    public void isReadyShouldReturnFalseWhenKameletsAreNotDeployedTest() {
        TestOpenshiftClient.setServer(expectServer);

        expectOperatorGet();
        expectKameletLs();
        expectKamelets(null);

        camelK.setClient(OpenshiftClient.get().adapt(CamelKClient.class));
        assertThat(camelK.isReady()).isFalse();
    }

    @Test
    public void kameletsReadyShouldReturnFalseWhenKameletIsntReadyTest() {
        TestOpenshiftClient.setServer(expectServer);

        expectOperatorGet();
        expectKameletLs();
        expectKamelets(false);

        camelK.setClient(OpenshiftClient.get().adapt(CamelKClient.class));
        assertThat(camelK.isReady()).isFalse();
    }

    @Test
    public void shouldBeReadyTest() {
        TestOpenshiftClient.setServer(expectServer);

        expectOperatorGet();
        expectKameletLs();
        expectKamelets(true);

        camelK.setClient(OpenshiftClient.get().adapt(CamelKClient.class));
        assertThat(camelK.isReady()).isTrue();
    }

    @Test
    public void shouldCreateAppFromIntegrationBuilderTest() {
        deployAndWait();

        executor.submit(() -> camelK.createIntegration(dummyIb()));

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> assertThat(OpenshiftClient.get().adapt(CamelKClient.class).v1()
            .integrations().withName(name())).isNotNull());
    }

    @Test
    public void shouldCreateAppFromKameletBindingTest() {
        deployAndWait();

        executor.submit(() -> camelK.createKameletBinding(new KameletBindingBuilder().withNewMetadata().withName("kb").endMetadata().build()));
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
        expectServer.expect().get().withPath("/api/v1/namespaces/test/pods/camel-k/exec?command=bash&command=-c&command="
                + Utils.toUrlEncoded(cmd).replace("+", "%20") + "&stdout=true&stderr=true")
            .andUpgradeToWebSocket()
            .open(new OutputStreamMessage("1"))
            .done().always();
    }

    private void expectKamelets(Object config) {
        final KameletListBuilder builder = new KameletListBuilder();
        if (config != null) {
            String phase = Boolean.parseBoolean(config.toString()) ? "Ready" : "Error";
            builder.addToItems(new KameletBuilder()
                .withNewMetadata()
                .withName(phase)
                .endMetadata()
                .withNewStatus()
                .withPhase(phase)
                .endStatus().build());
        }
        expectServer.expect().get().withPath("/apis/camel.apache.org/v1alpha1/namespaces/test/kamelets")
            .andReturn(200, builder.build()).always();
    }
}
