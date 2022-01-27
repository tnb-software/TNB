package org.jboss.fuse.tnb.product.cq;

import org.jboss.fuse.tnb.product.OpenshiftProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.cq.application.OpenshiftQuarkusApp;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class OpenshiftCamelQuarkus extends OpenshiftProduct {
    private static boolean initialized = false;

    @Override
    public boolean isReady() {
        return initialized;
    }

    @Override
    public void setupProduct() {
        if (!initialized) {
            Maven.setupMaven();
            initialized = true;
        }
    }

    @Override
    public void teardownProduct() {
    }

    @Override
    protected App createIntegrationApp(IntegrationBuilder integrationBuilder) {
        App app = new OpenshiftQuarkusApp(integrationBuilder);
        app.start();
        app.waitUntilReady();
        return app;
    }
}
