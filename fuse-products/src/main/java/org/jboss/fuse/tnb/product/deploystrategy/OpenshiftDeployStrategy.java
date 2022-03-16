package org.jboss.fuse.tnb.product.deploystrategy;

import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;

public interface OpenshiftDeployStrategy {
    ProductType[] products();

    OpenshiftDeployStrategyType deployType();

    default boolean applicable(AbstractIntegrationBuilder<?> integrationBuilder) {
        return true;
    }
}
