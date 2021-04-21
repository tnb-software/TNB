package org.jboss.fuse.tnb.product.ck;

import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.product.OpenshiftProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.ck.application.CamelKApp;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import cz.xtf.core.openshift.helpers.ResourceFunctions;

@AutoService(Product.class)
public class CamelK extends OpenshiftProduct {
    private static final Logger LOG = LoggerFactory.getLogger(CamelK.class);
    private App app;

    @Override
    public void setupProduct() {
        LOG.info("Deploying Camel-K");
        OpenshiftClient.createSubscription("stable", "camel-k", "community-operators", "test-camel-k");
        OpenshiftClient.waitForCompletion("test-camel-k");
    }

    @Override
    public void teardownProduct() {
        OpenshiftClient.deleteSubscription("test-camel-k");
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
