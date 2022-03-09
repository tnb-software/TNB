package org.jboss.fuse.tnb.product.cq;

import org.jboss.fuse.tnb.product.LocalProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.cq.application.LocalQuarkusApp;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class LocalCamelQuarkus extends LocalProduct {
    @Override
    protected App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        return new LocalQuarkusApp(integrationBuilder);
    }
}
