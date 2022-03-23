package org.jboss.fuse.tnb.product.deploystrategy;

import org.jboss.fuse.tnb.common.product.ProductType;

public interface OpenshiftDeployStrategy {
    ProductType[] products();

    OpenshiftDeployStrategyType deployType();
}
