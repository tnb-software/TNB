package org.jboss.fuse.tnb.product.standalone;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.jboss.fuse.tnb.product.Product;
import org.joor.Reflect;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class LocalCamelStandalone extends Product {
    private CamelContext context;

    @Override
    public void deploy() {
        context = new DefaultCamelContext();
    }

    @Override
    public void undeploy() {
    }

    @Override
    public void deployIntegration(Object route) {
        RouteBuilder builder = Reflect.compile("com.test.myroutebuilder", (String) route).create().get();
        try {
            context.addRoutes(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.start();
    }

    @Override
    public void waitForIntegration() {

    }

    @Override
    public void undeployIntegration() {
        context.stop();
    }
}
