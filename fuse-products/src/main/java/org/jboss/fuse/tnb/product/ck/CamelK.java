package org.jboss.fuse.tnb.product.ck;

import static org.jboss.fuse.tnb.common.utils.IOUtils.writeFile;
import static org.jboss.fuse.tnb.product.ck.configuration.CamelKConfiguration.SUBSCRIPTION_NAME;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
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
import org.jboss.fuse.tnb.product.ck.generated.Kamelet;
import org.jboss.fuse.tnb.product.ck.generated.KameletBinding;
import org.jboss.fuse.tnb.product.ck.generated.KameletList;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSettings;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSupport;
import org.jboss.fuse.tnb.product.ck.utils.OwnerReferenceSetter;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.interfaces.KameletOps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

@AutoService(Product.class)
public class CamelK extends OpenshiftProduct implements KameletOps {
    private static final Logger LOG = LoggerFactory.getLogger(CamelK.class);
    private static final CustomResourceDefinitionContext KAMELET_CONTEXT =
        CamelKSupport.kameletCRDContext(CamelKSettings.KAMELET_API_VERSION_DEFAULT);
    private static final CustomResourceDefinitionContext INTEGRATIONPLATFORM_CONTEXT =
        CamelKSupport.integrationPlatformCRDContext(CamelKSettings.API_VERSION_DEFAULT);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private NonNamespaceOperation<Kamelet, KameletList, Resource<Kamelet>> kameletClient;
    private final List<String> kamelets = new ArrayList<>();

    // Count of all kamelets from camel-k operator
    private int operatorKameletCount = -1;

    @Override
    public void setupProduct() {
        // Avoid creating static clients in case the camel-k should not be running (all product instances are created in ProductFactory.create()
        // and then the desired one is returned
        kameletClient = OpenshiftClient.get().customResources(KAMELET_CONTEXT, Kamelet.class, KameletList.class)
            .inNamespace(OpenshiftConfiguration.openshiftNamespace());

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

        // One-off use of IntegrationPlatform, so not creating classes for this small stuff
        // Create an IntegrationPlatform object with given maven repository or create maven settings configmap + reference it in the
        // integrationplatform
        // this object is created by "kamel install" or when installed through operatorhub, it's created with 1st integration
        Map<String, Object> metadata = Map.of(
            "labels", Map.of("app", "camel-k"),
            "name", "camel-k"
        );

        Map<String, Object> spec;
        if (TestConfiguration.mavenSettings() != null) {
            OpenshiftClient.get()
                .createConfigMap("tnb-maven-settings", Map.of("settings.xml", IOUtils.readFile(Paths.get(TestConfiguration.mavenSettings()))));
            spec = Map.of(
                "build", Map.of(
                    "maven", Map.of(
                        "settings", Map.of(
                            "configMapKeyRef", Map.of(
                                "key", "settings.xml",
                                "name", "tnb-maven-settings"
                            )
                        )
                    )
                )
            );
        } else {
            spec = Map.of(
                "build", Map.of("timeout", "30m"),
                "configuration", Collections.singletonList(Map.of(
                    "type", "repository",
                    "value", TestConfiguration.mavenRepository()
                ))
            );
        }
        Map<String, Object> integrationPlatform = Map.of(
            "apiVersion", "camel.apache.org/v1",
            "kind", "IntegrationPlatform",
            "metadata", metadata,
            "spec", spec
        );
        try {
            OpenshiftClient.get().customResource(INTEGRATIONPLATFORM_CONTEXT).delete(OpenshiftConfiguration.openshiftNamespace());
            OpenshiftClient.get().customResource(INTEGRATIONPLATFORM_CONTEXT)
                .create(OpenshiftConfiguration.openshiftNamespace(), integrationPlatform);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create integration platform: ", e);
        }
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

        return kameletClient.list().getItems().size() >= operatorKameletCount;
    }

    private boolean kameletsReady() {
        return kameletClient.list().getItems().stream().allMatch(k -> {
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
        return createIntegrations(new IntegrationBuilder[] {integrationBuilder}).get(integrationBuilder.getIntegrationName());
    }

    public Map<String, App> createIntegrations(IntegrationBuilder... integrationBuilders) {
        return createIntegrations((Object[]) integrationBuilders);
    }

    public Map<String, App> createKameletBindings(KameletBinding... kameletBindings) {
        return createIntegrations((Object[]) kameletBindings);
    }

    private Map<String, App> createIntegrations(Object... integrationSources) {
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
            LOG.error("Null kamelet");
            throw new RuntimeException("Null kamelet");
        }
        LOG.info("Creating Kamelet " + kamelet.getMetadata().getName());
        kameletClient.createOrReplace(kamelet);
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
        return kameletClient.withName(name).get();
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
        if (kameletClient != null) {
            kameletClient.withName(kameletName).delete();
        }
        kamelets.remove(kameletName);
    }

    public void removeKamelets() {
        if (kameletClient != null) {
            kamelets.forEach(kamelet -> kameletClient.withName(kamelet).delete());
        }
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
