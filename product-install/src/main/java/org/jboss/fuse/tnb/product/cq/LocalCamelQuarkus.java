package org.jboss.fuse.tnb.product.cq;

import org.jboss.fuse.tnb.product.Product;

import com.google.auto.service.AutoService;

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
    public void deployIntegration(Object route) {

    }

    @Override
    public void waitForIntegration() {

    }

    @Override
    public void undeployIntegration() {

    }
}
