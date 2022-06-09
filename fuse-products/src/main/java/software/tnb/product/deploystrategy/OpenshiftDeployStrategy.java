package software.tnb.product.deploystrategy;

import software.tnb.common.product.ProductType;

public interface OpenshiftDeployStrategy {
    ProductType[] products();

    OpenshiftDeployStrategyType deployType();
}
