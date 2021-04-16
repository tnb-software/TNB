package org.jboss.fuse.tnb.product.standalone;

import org.jboss.fuse.tnb.product.LocalProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.standalone.application.StandaloneApp;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class CamelStandalone extends LocalProduct {
    private App app;

    @Override
    public App createIntegration(IntegrationBuilder integrationBuilder) {
        app = new StandaloneApp(integrationBuilder);
        app.start();
        app.waitUntilReady();
        return app;
    }

    @Override
    public void removeIntegration() {
        app.stop();
    }
}
