package org.jboss.fuse.tnb.product.cq;

import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.product.Product;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;

@AutoService(Product.class)
public class OpenshiftCamelQuarkus extends Product implements OpenshiftDeployable {
    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean isDeployed() {
        return false;
    }

    @Override
    public void create() {

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
