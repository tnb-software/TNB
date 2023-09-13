package software.tnb.product.deploystrategy.impl;

import software.tnb.common.product.ProductType;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategy;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategyType;
import software.tnb.product.deploystrategy.impl.custom.CustomJKubeStrategy;

import com.google.auto.service.AutoService;

@AutoService(OpenshiftDeployStrategy.class)
public class JKubeStrategy extends CustomJKubeStrategy {

    public JKubeStrategy() {
        super(new String[]{"clean", "install"}, new String[]{"openshift"});
        createTnbDeployment(true);
    }

    @Override
    public ProductType[] products() {
        return new ProductType[] {ProductType.CAMEL_SPRINGBOOT};
    }

    @Override
    public OpenshiftDeployStrategyType deployType() {
        return OpenshiftDeployStrategyType.JKUBE;
    }
}
