package org.jboss.fuse.tnb.product.ck;

import com.google.auto.service.AutoService;

import cz.xtf.core.openshift.helpers.ResourceFunctions;

import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.product.OpenshiftProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.ck.application.CamelKApp;
import org.jboss.fuse.tnb.product.ck.configuration.CamelKConfiguration;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.fuse.tnb.product.ck.configuration.CamelKConfiguration.SUBSCRIPTION_NAME;

@AutoService(Product.class)
public class CamelK extends OpenshiftProduct {
    private static final Logger LOG = LoggerFactory.getLogger(CamelK.class);
    private App app;

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
        app.stop();
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("name", "camel-k-operator"));
    }
}
