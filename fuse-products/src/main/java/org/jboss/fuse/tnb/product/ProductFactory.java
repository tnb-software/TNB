package org.jboss.fuse.tnb.product;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public final class ProductFactory {
    private ProductFactory() {
    }

    /**
     * Creates an instance of a given product based on system properties set.
     *
     * @return product instance
     */
    public static Product create() {
        return create(Product.class);
    }

    /**
     * Creates an instance of a given product based on system properties set and returns the class specified as a parameter.
     *
     * @return product instance as given class
     */
    public static <P extends Product> P create(Class<P> clazz) {
        final Optional<Product> product = StreamSupport.stream(ServiceLoader.load(Product.class).spliterator(), false)
            .filter(p -> p.getClass().getSimpleName().toLowerCase().contains(TestConfiguration.product().getValue()))
            .filter(p -> p instanceof OpenshiftProduct == OpenshiftConfiguration.isOpenshift())
            .findFirst();
        if (product.isEmpty()) {
            // This fails while initializing the class and the exception will be swallowed and we only get the java.lang.ExceptionInInitializerError
            // so print the stacktrace manually before throwing the exception
            IllegalArgumentException e = new IllegalArgumentException(String.format("No Product class implementation for %s / %s found!",
                TestConfiguration.product(), OpenshiftConfiguration.isOpenshift() ? "OpenShift" : "local"));
            e.printStackTrace();
            throw e;
        } else {
            return clazz.cast(product.get());
        }
    }
}
