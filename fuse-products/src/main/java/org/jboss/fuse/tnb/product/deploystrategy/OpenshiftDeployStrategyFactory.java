package org.jboss.fuse.tnb.product.deploystrategy;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.product.interfaces.OpenshiftDeployer;

import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public abstract class OpenshiftDeployStrategyFactory {

    public static OpenshiftDeployer getDeployStrategy() {
        return (OpenshiftDeployer) StreamSupport.stream(ServiceLoader.load(OpenshiftDeployStrategy.class).spliterator(), false)
            .filter(s -> OpenshiftDeployStrategyType.valueOf(OpenshiftConfiguration.getDeployStrategy().toUpperCase()) == s.deployType())
            .filter(s -> Arrays.stream(s.products()).anyMatch(p -> p == TestConfiguration.product()))
            .findFirst().orElseThrow(() -> new RuntimeException("unable to find strategy implementation for "
                + OpenshiftConfiguration.getDeployStrategy()
                + " for product " + TestConfiguration.product().name()));
    }
}
