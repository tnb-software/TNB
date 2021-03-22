package org.jboss.fuse.tnb.product;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public final class ProductFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ProductFactory.class);

    private ProductFactory() {
    }

    public static Product create() {
        final Optional<Product> product = StreamSupport.stream(ServiceLoader.load(Product.class).spliterator(), false)
            .filter(p -> p.getClass().getSimpleName().toLowerCase().contains(TestConfiguration.product()))
            .filter(p -> p instanceof OpenshiftDeployable == OpenshiftConfiguration.isOpenshift())
            .findFirst();
        if (product.isEmpty()) {
            LOG.error("No Product class implementation for {} / {} found!", TestConfiguration.product(), OpenshiftConfiguration.isOpenshift() ? "openshift" : "local");
            throw new IllegalArgumentException();
        } else {
            return product.get();
        }
    }
}
