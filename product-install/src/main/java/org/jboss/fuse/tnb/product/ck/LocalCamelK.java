package org.jboss.fuse.tnb.product.ck;

import org.jboss.fuse.tnb.product.Product;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;

@AutoService(Product.class)
public class LocalCamelK extends Product {
    @Override
    public void deploy() {
        throw new IllegalArgumentException("Camel-K local deployment is not supported");
    }

    @Override
    public void undeploy() {
    }

    @Override
    public void deployIntegration(String name, CodeBlock routeDefinition, String... camelComponents) {
    }

    @Override
    public void waitForIntegration(String name) {
    }

    @Override
    public void undeployIntegration() {
    }
}
