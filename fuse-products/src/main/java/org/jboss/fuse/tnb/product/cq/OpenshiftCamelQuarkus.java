package org.jboss.fuse.tnb.product.cq;

import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.OpenshiftProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.cq.application.OpenshiftQuarkusApp;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import cz.xtf.core.openshift.helpers.ResourceFunctions;

@AutoService(Product.class)
public class OpenshiftCamelQuarkus extends OpenshiftProduct {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCamelQuarkus.class);

    private static boolean initialized;

    private App app;

    @Override
    public boolean isReady() {
        return initialized;
    }

    @Override
    public void setupProduct() {
        Maven.setupMaven();
        initialized = true;
    }

    @Override
    public void teardownProduct() {
    }

    @Override
    public App createIntegration(IntegrationBuilder integrationBuilder) {
        app = new OpenshiftQuarkusApp(integrationBuilder);
        app.start();
        app.waitUntilReady();
        return app;
    }

    public void waitForIntegration(String name) {
        LOG.info("Waiting until integration {} is running", name);
        WaitUtils.waitFor(() -> {
            if (ResourceFunctions.areExactlyNPodsRunning(1).apply(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name))) {
                return OpenshiftClient.getLogs(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name).get(0))
                    .contains("started and consuming");
            } else {
                return false;
            }
        }, 24, 5000L, String.format("Waiting until the integration %s is running", name));
    }

    @Override
    public void removeIntegrations() {
        if (app != null) {
            app.stop();
        }
    }
}
