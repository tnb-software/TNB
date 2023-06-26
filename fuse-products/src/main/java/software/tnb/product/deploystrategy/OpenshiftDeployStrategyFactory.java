package software.tnb.product.deploystrategy;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.config.TestConfiguration;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.interfaces.OpenshiftDeployer;

import java.util.Arrays;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public abstract class OpenshiftDeployStrategyFactory {

    public static OpenshiftDeployer getDeployStrategy(final AbstractIntegrationBuilder<?> integrationBuilder) {
        return (Objects.nonNull(integrationBuilder.getOCPCustomStrategy()) ? integrationBuilder.getOCPCustomStrategy()
            : ((OpenshiftDeployer) StreamSupport.stream(ServiceLoader.load(OpenshiftDeployStrategy.class).spliterator(), false)
            .filter(s -> OpenshiftDeployStrategyType.valueOf(OpenshiftConfiguration.getDeployStrategy().toUpperCase()) == s.deployType())
            .filter(s -> Arrays.stream(s.products()).anyMatch(p -> p == TestConfiguration.product()))
            .findFirst().orElseThrow(() -> new RuntimeException("unable to find strategy implementation for "
                + OpenshiftConfiguration.getDeployStrategy()
                + " for product " + TestConfiguration.product().name()))))
            .setIntegrationBuilder(integrationBuilder);
    }
}
