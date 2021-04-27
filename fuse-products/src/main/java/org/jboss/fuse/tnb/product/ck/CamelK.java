package org.jboss.fuse.tnb.product.ck;

import com.google.auto.service.AutoService;

import cz.xtf.core.openshift.helpers.ResourceFunctions;

import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.fuse.tnb.product.ck.configuration.CamelKConfiguration.SUBSCRIPTION_NAME;
import java.io.File;
import java.io.FileInputStream;

@AutoService(Product.class)
public class CamelK extends OpenshiftProduct implements KameletOps {
    private static final Logger LOG = LoggerFactory.getLogger(CamelK.class);
    private App app;
    private static OpenshiftClient client = OpenshiftClient.get();
    private static CustomResourceDefinitionContext kameletCtx = CamelKSupport.kameletCRDContext(CamelKSettings.KAMELET_API_VERSION_DEFAULT);
    private static CustomResourceDefinitionContext kameletBindingCtx =
        CamelKSupport.kameletBindingCRDContext(CamelKSettings.KAMELET_API_VERSION_DEFAULT);
    private static final NonNamespaceOperation<Kamelet, KameletList, Resource<Kamelet>>
        kameletClient = client.customResources(kameletCtx, Kamelet.class, KameletList.class).inNamespace(client.getNamespace());
    private static final NonNamespaceOperation<KameletBinding, KameletBindingList, Resource<KameletBinding>> kameletBindingClient =
        client.customResources(kameletBindingCtx, KameletBinding.class, KameletBindingList.class).inNamespace(client.getNamespace());

    @Override
    public void setupProduct() {
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
            .createSubscription(config.subscriptionChannel(), config.subscriptionOperatorName(), config.subscriptionSource(), SUBSCRIPTION_NAME,
                config.subscriptionSourceNamespace());
        OpenshiftClient.waitForCompletion(SUBSCRIPTION_NAME);
    }

    @Override
    public void teardownProduct() {
        OpenshiftClient.deleteSubscription(SUBSCRIPTION_NAME);
    }

    @Override
    public App createIntegration(IntegrationBuilder integrationBuilder) {
        app = new CamelKApp(integrationBuilder);
        app.start();
        app.waitUntilReady();
        return app;
    }

    @Override
    public void removeIntegration() {
        if (app != null) {
            app.stop();
        }
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("name", "camel-k-operator"));
    }

    @Override
    public void loadKamelet(File file) {
        if (file == null) {
            LOG.error("File is null");
            throw new RuntimeException("File is null");
        }
        try {
            Kamelet k = kameletClient.load(new FileInputStream(file)).get();
            createKamelet(k);
        } catch (Exception e) {
            LOG.error("Failed to load Kamelet");
            throw new RuntimeException("Failed to load Kamelet", e);
        }
    }

    @Override
    public void loadKameletBinding(File file) {
        if (file == null) {
            LOG.error("File is null");
            throw new RuntimeException("File is null");
        }
        try {
            KameletBinding kb = kameletBindingClient.load(new FileInputStream(file)).get();
            createKameletBinding(kb);
        } catch (Exception e) {
            LOG.error("Failed to load KameletBinding");
            throw new RuntimeException("Failed to load KameletBinding: ", e);
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
        WaitUtils.waitFor(() -> isKameletReady(kamelet), "Waiting for Kamelet to be ready");
    }

    @Override
    public void createKameletBinding(KameletBinding kameletBinding) {
        if (kameletBinding == null) {
            LOG.error("Null KameletBinding");
            throw new RuntimeException("Null KameletBinding");
        }
        LOG.info("Create KameletBinding " + kameletBinding.getMetadata().getName());
        kameletBindingClient.createOrReplace(kameletBinding);
        WaitUtils.waitFor(() -> isKameletBindingReady(kameletBinding), 50, 5000L,
            "Waiting for KameletBinding to be ready");//TODO maybe not needed here - could run in parallel with others
    }

    @Override
    public void deleteKamelet(String name) {
        LOG.info("Delete Kamelet " + name);
        kameletClient.withName(name).delete();
    }

    @Override
    public void deleteKamelet(Kamelet kamelet) {
        if (kamelet != null) {
            LOG.info("Delete Kamelet " + kamelet.getMetadata().getName());
            kameletClient.delete(kamelet);
        }
    }

    @Override
    public void deleteKameletBinding(String kameletBinding) {
        LOG.info("Delete KameletBinding " + kameletBinding);
        kameletBindingClient.withName(kameletBinding).delete();
    }

    @Override
    public void deleteKameletBinding(KameletBinding kameletBinding) {
        if (kameletBinding != null) {
            LOG.info("Delete KameletBinding " + kameletBinding.getMetadata().getName());
            kameletBindingClient.delete(kameletBinding);
        }
    }

    @Override
    public boolean isKameletBindingReady(KameletBinding kameletBinding) {
        String kameletBindingName = kameletBinding.getMetadata().getName();
        if ((kameletBindingClient.withName(kameletBindingName).get() != null) &&
            (kameletBindingClient.withName(kameletBindingName).get().getStatus() != null)) {
            return "ready".equalsIgnoreCase(kameletBindingClient.withName(kameletBindingName).get().getStatus().getPhase());
        } else {
            return false;
        }
    }

    @Override
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
    @Override
    public Kamelet getKameletByName(String name) {
        return kameletClient.withName(name).get();
    }

    /**
     * @param name of KameletBinding
     * @return null if KameletBinding wasn't found, otherwise KameletBinding with given name
     */
    @Override
    public KameletBinding getKameletBindingByName(String name) {
        return kameletBindingClient.withName(name).get();
    }
}
