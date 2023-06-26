package software.tnb.product.deploystrategy.impl.custom;

import software.tnb.common.product.ProductType;
import software.tnb.product.deploystrategy.OpenshiftBaseDeployer;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategyType;

public abstract class OpenshiftCustomDeployer extends OpenshiftBaseDeployer {

    @Override
    public OpenshiftDeployStrategyType deployType() {
        return OpenshiftDeployStrategyType.CUSTOM;
    }

    @Override
    public ProductType[] products() {
        return new ProductType[] {ProductType.CAMEL_SPRINGBOOT};
    }
}
