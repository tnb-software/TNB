package org.jboss.fuse.tnb.product.standalone;

import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.product.Product;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class OpenshiftCamelStandalone extends Product implements OpenshiftDeployable {
    @Override
    public void create() {
        throw new IllegalArgumentException("Camel Standalone OpenShift deployment is not supported");
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

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean isDeployed() {
        return false;
    }
}
