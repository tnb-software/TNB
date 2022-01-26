package org.jboss.fuse.tnb.product.csb;

import org.jboss.fuse.tnb.product.LocalProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.csb.application.LocalSpringBootApp;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class LocalCamelSpringBoot extends LocalProduct {
    private static final Logger LOG = LoggerFactory.getLogger(LocalCamelSpringBoot.class);
    private App app;

    public App createIntegration(IntegrationBuilder integrationBuilder) {
        app = new LocalSpringBootApp(integrationBuilder);
        app.start();
        app.waitUntilReady();
        return app;
    }

    @Override
    public void removeIntegrations() {
        if (app != null) {
            app.stop();
        }
    }
}
