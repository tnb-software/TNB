package org.jboss.fuse.tnb.product.ck;

import static org.jboss.fuse.tnb.common.utils.IOUtils.writeFile;
import static org.jboss.fuse.tnb.product.ck.configuration.CamelKConfiguration.SUBSCRIPTION_NAME;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.common.utils.PropertiesUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.OpenshiftProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.ck.application.CamelKApp;
import org.jboss.fuse.tnb.product.ck.configuration.CamelKConfiguration;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSupport;
import org.jboss.fuse.tnb.product.ck.utils.OwnerReferenceSetter;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.interfaces.KameletOps;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.auto.service.AutoService;

import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.xtf.core.openshift.PodShellOutput;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.camelk.client.CamelKClient;
import io.fabric8.camelk.v1.IntegrationPlatform;
import io.fabric8.camelk.v1.IntegrationPlatformBuilder;
import io.fabric8.camelk.v1.IntegrationPlatformSpecBuilder;
import io.fabric8.camelk.v1alpha1.Kamelet;
import io.fabric8.camelk.v1alpha1.KameletBinding;
import io.fabric8.kubernetes.api.model.ConfigMapKeySelector;
import io.fabric8.kubernetes.api.model.Duration;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.utils.Serialization;

@AutoService(Product.class)
public class CamelK extends OpenshiftProduct implements KameletOps {
    private static final Logger LOG = LoggerFactory.getLogger(CamelK.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final List<String> kamelets = new ArrayList<>();

    // Count of all kamelets from camel-k operator
    private int operatorKameletCount = -1;

    private CamelKClient camelKClient;

    @Override
    public void setupProduct() {
        // Avoid creating static clients in case the camel-k should not be running (all product instances are created in ProductFactory.create()
        // and then the desired one is returned
        camelKClient = OpenshiftClient.get().adapt(CamelKClient.class);

        if (!isReady()) {
            LOG.info("Deploying Camel-K");
            CamelKConfiguration config = CamelKConfiguration.getConfiguration();
            if (CamelKConfiguration.forceUpstream()) {
                LOG.warn(
                    "You are going to deploy upstream version of Camel-K. "
                        + "Be aware that upstream Camel-K APIs does not have to be compatible with the PROD ones and this installation can break the "
                        + "cluster for other tests."
                );
            }
            if (OpenshiftClient.get().operatorHub().catalogSources().inNamespace(config.subscriptionSourceNamespace())
                .withName(config.subscriptionSource()).get() == null) {
                LOG.error("Operator Hub catalog source {} not found! Set {} property to an existing catalog source or create a new catalog source"
                        + " with {} name in {} namespace. Be careful, as if someone else uses the same cluster with a different version,"
                        + " the deployments may fail due changes in CRDs between versions", config.subscriptionSource(),
                    CamelKConfiguration.SUBSCRIPTION_SOURCE, config.subscriptionSource(), config.subscriptionSourceNamespace());
                throw new RuntimeException("Operator Hub catalog source " + config.subscriptionSource() + " not found!");
            }
            OpenshiftClient.get().createSubscription(config.subscriptionChannel(), config.subscriptionOperatorName(), config.subscriptionSource(),
                SUBSCRIPTION_NAME,
                config.subscriptionSourceNamespace());
            OpenshiftClient.get().waitForInstallPlanToComplete(SUBSCRIPTION_NAME);
        }

        // @formatter:off
        IntegrationPlatform ip = new IntegrationPlatformBuilder()
            .withNewMetadata()
                .withLabels(Map.of("app", "camel-k"))
                .withName("camel-k")
            .endMetadata()
            .build();

        IntegrationPlatformSpecBuilder specBuilder = new IntegrationPlatformSpecBuilder()
            .withNewBuild()
                .withTimeout(new Duration(java.time.Duration.of(30, ChronoUnit.MINUTES)))
            .endBuild();

        if (TestConfiguration.mavenSettings() == null) {
            OpenshiftClient.get().createConfigMap("tnb-maven-settings", Map.of("settings.xml", Maven.createSettingsXmlFile()));
        } else {
            OpenshiftClient.get()
                .createConfigMap("tnb-maven-settings", Map.of("settings.xml", IOUtils.readFile(Paths.get(TestConfiguration.mavenSettings()))));
        }

        specBuilder
            .editBuild()
                .withNewMaven()
                    .withNewSettings()
                        .withConfigMapKeyRef(new ConfigMapKeySelector("settings.xml", "tnb-maven-settings", false))
                    .endSettings()
                .endMaven()
            .endBuild()
            .build();
        // @formatter:on

        ip.setSpec(specBuilder.build());

        camelKClient.v1().integrationPlatforms().delete();
        camelKClient.v1().integrationPlatforms().create(ip);
    }

    private boolean kameletsDeployed() {
        if (operatorKameletCount == -1) {
            final PodShellOutput shellOutput =
                OpenshiftClient.get().podShell(OpenshiftClient.get().getLabeledPods("name", "camel-k-operator").get(0))
                    .executeWithBash("ls /kamelets/* | wc -l");
            if (!shellOutput.getError().isEmpty()) {
                LOG.error("Unable to list all kamelets: {}", shellOutput.getError());
                return false;
            }
            if (shellOutput.getOutput().isEmpty()) {
                LOG.error("Unable to list all kamelets: empty response");
                return false;
            }

            operatorKameletCount = Integer.parseInt(shellOutput.getOutput().trim());
        }

        // https://github.com/fabric8io/kubernetes-client/issues/3852
        Serialization.jsonMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return camelKClient.v1alpha1().kamelets().list().getItems().size() >= operatorKameletCount;
    }

    private boolean kameletsReady() {
        return camelKClient.v1alpha1().kamelets().list().getItems().stream().allMatch(k -> {
            if (k.getStatus() == null) {
                return false;
            }
            return "Ready".equals(k.getStatus().getPhase());
        });
    }

    @Override
    public void teardownProduct() {
        if (!TestConfiguration.skipTearDown()) {
            saveOperatorLog();
            OpenshiftClient.get().deleteSubscription(SUBSCRIPTION_NAME);
            removeKamelets();
        }
    }

    @Override
    protected App createIntegrationApp(IntegrationBuilder integrationBuilder) {
        return this.createIntegration(integrationBuilder);
    }

    @Override
    public App createIntegration(IntegrationBuilder integrationBuilder) {
        return createIntegration(integrationBuilder, new IntegrationBuilder[] {}).get(integrationBuilder.getIntegrationName());
    }

    @Override
    public Map<String, App> createIntegration(IntegrationBuilder integrationBuilder, IntegrationBuilder... integrationBuilders) {
        List<Object> integrationSources = new ArrayList<>();
        integrationSources.add(integrationBuilder);
        integrationSources.addAll(Arrays.asList(integrationBuilders));
        return createIntegration(integrationSources.toArray());
    }

    public Map<String, App> createKameletBindings(KameletBinding... kameletBindings) {
        return createIntegration((Object[]) kameletBindings);
    }

    private Map<String, App> createIntegration(Object... integrationSources) {
        // Return only integrations created in this invocation, not all created integrations
        Map<String, App> apps = new HashMap<>();
        for (Object integrationSource : integrationSources) {
            App app = createApp(integrationSource);
            apps.put(app.getName(), app);
            app.start();
        }

        apps.values().forEach(App::waitUntilReady);
        integrations.putAll(apps);

        return apps;
    }

    public App createKameletBinding(KameletBinding kameletBinding) {
        return createKameletBindings(new KameletBinding[] {kameletBinding}).get(kameletBinding.getMetadata().getName());
    }

    private App createApp(Object integrationSource) {
        App app;
        if (integrationSource instanceof IntegrationBuilder) {
            app = new CamelKApp((IntegrationBuilder) integrationSource);
        } else if (integrationSource instanceof KameletBinding) {
            app = new CamelKApp((KameletBinding) integrationSource);
        } else {
            throw new IllegalArgumentException("Creating Camel-K integrations is possible only with IntegrationBuilders and KameletBindings (was "
                + integrationSource.getClass().getSimpleName() + ")");
        }
        integrations.put(app.getName(), app);
        return app;
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("name", "camel-k-operator"))
            && kameletsDeployed() && kameletsReady();
    }

    @Override
    public void createKamelet(Kamelet kamelet) {
        if (kamelet == null) {
            throw new RuntimeException("Null kamelet");
        }
        LOG.info("Creating Kamelet " + kamelet.getMetadata().getName());
        camelKClient.v1alpha1().kamelets().createOrReplace(kamelet);
        kamelets.add(kamelet.getMetadata().getName());
        WaitUtils.waitFor(() -> isKameletReady(kamelet), "Waiting for Kamelet to be ready");
    }

    public boolean isKameletReady(Kamelet kamelet) {
        if (kamelet == null) {
            return false;
        }
        String kameletName = kamelet.getMetadata().getName();
        if (getKameletByName(kameletName) != null && getKameletByName(kameletName).getStatus() != null) {
            return "ready".equalsIgnoreCase(getKameletByName(kameletName).getStatus().getPhase());
        } else {
            return false;
        }
    }

    @Override
    public boolean isKameletReady(String name) {
        return isKameletReady(getKameletByName(name));
    }

    /**
     * Gets kamelet by its name.
     *
     * @param name of Kamelet
     * @return null if Kamelet wasn't found, otherwise Kamelet with given name
     */
    public Kamelet getKameletByName(String name) {
        return camelKClient.v1alpha1().kamelets().withName(name).get();
    }

    /**
     * Create and label secret from credentials to kamelet.
     *
     * @param kameletName name of kamelet
     * @param credentials credentials required by kamelet (keys may contain underscore)
     */
    @Override
    public void createApplicationPropertiesSecretForKamelet(String kameletName, Properties credentials) {
        String prefix = "camel.kamelet." + kameletName + "." + kameletName + ".";
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put(CamelKSupport.CAMELK_CRD_GROUP + "/kamelet", kameletName);
        labels.put(CamelKSupport.CAMELK_CRD_GROUP + "/kamelet.configuration", kameletName);
        Properties camelCaseCredentials = PropertiesUtils.toCamelCaseProperties(credentials);

        // Set the later created integration object as the owner of the secret, so that the secret is deleted together with the integration
        Secret integrationSecret =
            OpenshiftClient.get().createApplicationPropertiesSecret(kameletName + "." + kameletName, camelCaseCredentials, labels, prefix);
        EXECUTOR_SERVICE.submit(new OwnerReferenceSetter(integrationSecret, kameletName));
    }

    @Override
    public void deleteSecretForKamelet(String kameletName) {
        OpenshiftClient.get().deleteSecret(kameletName + "." + kameletName);
    }

    @Override
    public void removeKamelet(String kameletName) {
        LOG.info("Deleting Kamelet " + kameletName);
        camelKClient.v1alpha1().kamelets().withName(kameletName).delete();
        kamelets.remove(kameletName);
    }

    public void removeKamelets() {
        kamelets.forEach(kamelet -> camelKClient.v1alpha1().kamelets().withName(kamelet).delete());
        kamelets.clear();
    }

    @Override
    public void removeIntegrations() {
        CountDownLatch latch = new CountDownLatch(integrations.size());
        integrations.values().forEach(app -> EXECUTOR_SERVICE.submit(() -> {
            try {
                app.stop();
            } finally {
                latch.countDown();
            }
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.warn("Latch await thread interrupted");
        }
        integrations.clear();
    }

    private void saveOperatorLog() {
        LOG.info("Collecting logs of camel-k-operator");
        writeFile(TestConfiguration.appLocation().resolve("camel-k-operator.log"),
            OpenshiftClient.get().getLogs(OpenshiftClient.get().getLabeledPods("name", "camel-k-operator").get(0)));
    }
}
