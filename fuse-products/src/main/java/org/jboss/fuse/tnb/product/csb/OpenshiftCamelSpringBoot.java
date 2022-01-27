package org.jboss.fuse.tnb.product.csb;

import org.jboss.fuse.tnb.product.OpenshiftProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.csb.application.OpenshiftSpringBootApp;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class OpenshiftCamelSpringBoot extends OpenshiftProduct {
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
        App app = new OpenshiftSpringBootApp(integrationBuilder);
        app.start();
        app.waitUntilReady();
        return app;
    }
}
