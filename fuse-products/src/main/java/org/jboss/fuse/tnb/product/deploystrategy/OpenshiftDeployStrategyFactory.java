package org.jboss.fuse.tnb.product.deploystrategy;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.interfaces.OpenshiftDeployer;

import org.junit.platform.commons.util.ClassFilter;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class OpenshiftDeployStrategyFactory {

    private static final String STRATEGIES_PACKAGE = "org.jboss.fuse.tnb.product.deploystrategy.impl";

    public static OpenshiftDeployer getDeployStrategy() {
        try {
            return getStrategy(OpenshiftDeployStrategy.valueOf(OpenshiftConfiguration.getDeployStrategy().toUpperCase()));
        } catch (NoSuchMethodException | InvocationTargetException
            | InstantiationException | IllegalAccessException e) {
            throw  new RuntimeException(e);
        }
    }

    private static OpenshiftDeployer getStrategy(OpenshiftDeployStrategy strategyType)
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final ProductType product = TestConfiguration.product();
        List<Class<?>> strategiesForProduct = getStrategiesForProduct(product);
        Class<?> strategyCls = strategiesForProduct.stream()
            .filter(aClass -> aClass.getAnnotation(DeployableWith.class).strategy() == strategyType)
            .findFirst().orElseThrow(() -> new RuntimeException("unable to find strategy implementation for " + strategyType
                + " for product " + product));
        return (OpenshiftDeployer) strategyCls.getDeclaredConstructor().newInstance();
    }

    private static List<Class<?>> getStrategiesForProduct(ProductType productType) {
        List<Class<?>> deployableClasses = ReflectionUtils.findAllClassesInPackage(STRATEGIES_PACKAGE
            , ClassFilter.of(cls -> OpenshiftDeployer.class.isAssignableFrom(cls)));

        return deployableClasses.stream().filter(aClass -> aClass.getAnnotation(DeployableWith.class) != null
            && Arrays.stream(aClass.getAnnotation(DeployableWith.class).product())
            .anyMatch(product -> product == productType)
        ).collect(Collectors.toList());
    }
}
