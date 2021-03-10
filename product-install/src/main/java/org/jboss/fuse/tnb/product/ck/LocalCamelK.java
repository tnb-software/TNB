package org.jboss.fuse.tnb.product.ck;

import org.jboss.fuse.tnb.product.Product;

import com.google.auto.service.AutoService;

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
    public void deployIntegration(Object route) {
    }

    @Override
    public void waitForIntegration() {
    }

    @Override
    public void undeployIntegration() {
    }
}
