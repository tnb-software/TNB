package org.jboss.fuse.tnb.product;

import org.jboss.fuse.tnb.product.steps.Step;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Product implements BeforeAllCallback, AfterAllCallback, AfterEachCallback {
    private static final Logger LOG = LoggerFactory.getLogger(Product.class);

    public <U extends Step> void runStep(U s) {
        LOG.error("Unsupported step " + s.getClass()); //TODO behavior in this case isn't clear yet
    }

    public abstract void setupProduct();

    public abstract void teardownProduct();

}
