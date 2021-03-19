package org.jboss.fuse.tnb.product.cq;

import org.jboss.fuse.tnb.product.Product;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;

@AutoService(Product.class)
public class LocalCamelQuarkus extends Product {
    @Override
    public void deploy() {
        System.out.println("local camel quarkus deploy");
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
