package org.jboss.fuse.tnb.product.ck;

import static org.jboss.fuse.tnb.product.ck.configuration.CamelKConfiguration.SUBSCRIPTION_NAME;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.OpenshiftProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.ck.application.CamelKApp;
import org.jboss.fuse.tnb.product.ck.configuration.CamelKConfiguration;
import org.jboss.fuse.tnb.product.ck.generated.Kamelet;
import org.jboss.fuse.tnb.product.ck.generated.KameletBinding;
import org.jboss.fuse.tnb.product.ck.generated.KameletBindingList;
import org.jboss.fuse.tnb.product.ck.generated.KameletList;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSettings;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSupport;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.interfaces.KameletOps;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

@AutoService(Product.class)
public class CamelK extends OpenshiftProduct implements KameletOps {
    private static final Logger LOG = LoggerFactory.getLogger(CamelK.class);
    private static final CustomResourceDefinitionContext KAMELET_CONTEXT =
        CamelKSupport.kameletCRDContext(CamelKSettings.KAMELET_API_VERSION_DEFAULT);
    private static final CustomResourceDefinitionContext KAMELET_BINDING_CONTEXT =
        CamelKSupport.kameletBindingCRDContext(CamelKSettings.KAMELET_API_VERSION_DEFAULT);
    private static final CustomResourceDefinitionContext INTEGRATIONPLATFORM_CONTEXT =
        CamelKSupport.integrationPlatformCRDContext(CamelKSettings.API_VERSION_DEFAULT);

    private NonNamespaceOperation<Kamelet, KameletList, Resource<Kamelet>> kameletClient;
    private NonNamespaceOperation<KameletBinding, KameletBindingList, Resource<KameletBinding>> kameletBindingClient;
    private final Map<String, CamelKApp> integrations = new HashMap<>();
    private final List<String> kamelets = new ArrayList<>();

    @Override
    public void setupProduct() {
        if (!isReady()) {
            LOG.info("Deploying Camel-K");
            CamelKConfiguration config = CamelKConfiguration.getConfiguration();
            if (CamelKConfiguration.forceUpstream()) {
                LOG.warn(
                    "You are going to deploy upstream version of Camel-K. " +
                        "Be aware that upstream Camel-K APIs does not have to be compatible with the PROD ones and this installation can break the " +
                        "cluster for other tests."
                );
            }
            OpenshiftClient
                .createSubscription(config.subscriptionChannel(), config.subscriptionOperatorName(), config.subscriptionSource(),
                    SUBSCRIPTION_NAME,
                    config.subscriptionSourceNamespace());
            OpenshiftClient.waitForInstallPlanToComplete(SUBSCRIPTION_NAME);
        }

        // Avoid creating static clients in case the camel-k should not be running (all product instances are created in ProductFactory.create()
        // and then the desired one is returned
        kameletClient = OpenshiftClient.get().customResources(KAMELET_CONTEXT, Kamelet.class, KameletList.class)
            .inNamespace(OpenshiftConfiguration.openshiftNamespace());
        kameletBindingClient = OpenshiftClient.get().customResources(KAMELET_BINDING_CONTEXT, KameletBinding.class, KameletBindingList.class)
            .inNamespace(OpenshiftConfiguration.openshiftNamespace());

        if (TestConfiguration.mavenRepository() != null) {
            // One-off use of IntegrationPlatform, so not creating classes for this small stuff
            // Create an IntegrationPlatform object with given maven repository
            // this object is created by "kamel install" or when installed through operatorhub, it's created with 1st integration
            Map<String, Object> metadata = Map.of(
                "labels", Map.of("app", "camel-k"),
                "name", "camel-k"
            );
            Map<String, Object> spec = Map.of(
                "configuration", Collections.singletonList(Map.of(
                    "type", "repository",
                    "value", TestConfiguration.mavenRepository()
                ))
            );
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
    }

    @Override
    public void teardownProduct() {
        OpenshiftClient.deleteSubscription(SUBSCRIPTION_NAME);
        removeKamelets();
    }

    @Override
    public App createIntegration(IntegrationBuilder integrationBuilder) {
        CamelKApp app = new CamelKApp(integrationBuilder);
        integrations.put(integrationBuilder.getIntegrationName(), app);
        app.start();
        app.waitUntilReady();
        return app;
    }

    public App createKameletBinding(KameletBinding kameletBinding) {
        CamelKApp app = new CamelKApp(kameletBinding);
        integrations.put(kameletBinding.getMetadata().getName(), app);
        app.start();
        app.waitUntilReady();
        return app;
    }

    public App createKameletBinding(File kameletBindingFile) {
        if (kameletBindingFile == null) {
            LOG.error("File is null");
            throw new RuntimeException("File is null");
        }
        try (FileInputStream is = new FileInputStream(kameletBindingFile)) {
            return createKameletBinding(kameletBindingClient.load(is).get());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load KameletBinding: ", e);
        }
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("name", "camel-k-operator"));
    }

    @Override
    public void createKamelet(File file) {
        if (file == null) {
            LOG.error("File is null");
            throw new RuntimeException("File is null");
        }
        try (FileInputStream is = new FileInputStream(file)) {
            createKamelet(kameletClient.load(file).get());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Kamelet", e);
        }
    }

    @Override
    public void createKamelet(Kamelet kamelet) {
        if (kamelet == null) {
            LOG.error("Null kamelet");
            throw new RuntimeException("Null kamelet");
        }
        LOG.info("Create Kamelet " + kamelet.getMetadata().getName());
        kameletClient.createOrReplace(kamelet);
        kamelets.add(kamelet.getMetadata().getName());
        WaitUtils.waitFor(() -> isKameletReady(kamelet), "Waiting for Kamelet to be ready");
    }

    public boolean isKameletReady(Kamelet kamelet) {
        String kameletName = kamelet.getMetadata().getName();
        if ((getKameletByName(kameletName) != null) &&
            (getKameletByName(kameletName).getStatus() != null)
        ) {
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
     * @param name of Kamelet
     * @return null if Kamelet wasn't found, otherwise Kamelet with given name
     */
    public Kamelet getKameletByName(String name) {
        return kameletClient.withName(name).get();
    }

    /**
     * Create and label secret from credentials to kamelet
     *
     * @param kameletName name of kamelet
     * @param credentials credentials required by kamelet
     */
    @Override
    public void createApplicationPropertiesSecretForKamelet(String kameletName, Properties credentials) {
        String prefix = "camel.kamelet." + kameletName + "." + kameletName + ".";
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put(CamelKSupport.CAMELK_CRD_GROUP + "/kamelet", kameletName);
        labels.put(CamelKSupport.CAMELK_CRD_GROUP + "/kamelet.configuration", kameletName);
        OpenshiftClient.createApplicationPropertiesSecret(kameletName + "." + kameletName, credentials, labels, prefix);
    }

    @Override
    public void deleteSecretForKamelet(String kameletName) {
        OpenshiftClient.deleteSecret(kameletName + "." + kameletName);
    }

    @Override
    public void removeKamelet(String kameletName) {
        LOG.info("Delete Kamelet " + kameletName);
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

    public void removeIntegration(String name) {
        integrations.get(name).stop();
        integrations.remove(name);
    }

    @Override
    public void removeIntegrations() {
        integrations.values().forEach(CamelKApp::stop);//can't be reused removeIntegration - changing underlying map
        integrations.clear();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        removeIntegrations();
    }
}
