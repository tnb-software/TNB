package software.tnb.product.junit.jira;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.config.TestConfiguration;
import software.tnb.common.product.ProductType;
import software.tnb.product.cq.configuration.QuarkusConfiguration;

public enum ProductConfiguration {
    ALL,
    QUARKUS, QUARKUS_JVM, QUARKUS_JVM_LOCAL, QUARKUS_JVM_OPENSHIFT, QUARKUS_NATIVE, QUARKUS_NATIVE_LOCAL, QUARKUS_NATIVE_OPENSHIFT,
    CAMEL_K,
    SPRINGBOOT, SPRINGBOOT_JVM_LOCAL, SPRINGBOOT_JVM_OPENSHIFT;

    public boolean isCurrentEnv() {
        return switch (this) {
            case QUARKUS -> TestConfiguration.product() == ProductType.CAMEL_QUARKUS;
            case QUARKUS_JVM -> TestConfiguration.product() == ProductType.CAMEL_QUARKUS
                && !QuarkusConfiguration.isQuarkusNative();
            case QUARKUS_JVM_LOCAL -> TestConfiguration.product() == ProductType.CAMEL_QUARKUS
                && !QuarkusConfiguration.isQuarkusNative() && !OpenshiftConfiguration.isOpenshift();
            case QUARKUS_JVM_OPENSHIFT -> TestConfiguration.product() == ProductType.CAMEL_QUARKUS
                && !QuarkusConfiguration.isQuarkusNative() && OpenshiftConfiguration.isOpenshift();
            case QUARKUS_NATIVE -> TestConfiguration.product() == ProductType.CAMEL_QUARKUS
                && QuarkusConfiguration.isQuarkusNative();
            case QUARKUS_NATIVE_LOCAL -> TestConfiguration.product() == ProductType.CAMEL_QUARKUS
                && QuarkusConfiguration.isQuarkusNative() && !OpenshiftConfiguration.isOpenshift();
            case QUARKUS_NATIVE_OPENSHIFT -> TestConfiguration.product() == ProductType.CAMEL_QUARKUS
                && QuarkusConfiguration.isQuarkusNative()
                && OpenshiftConfiguration.isOpenshift();
            case CAMEL_K -> TestConfiguration.product() == ProductType.CAMEL_K;
            case SPRINGBOOT -> TestConfiguration.product() == ProductType.CAMEL_SPRINGBOOT;
            case SPRINGBOOT_JVM_LOCAL -> TestConfiguration.product() == ProductType.CAMEL_SPRINGBOOT
                && !OpenshiftConfiguration.isOpenshift();
            case SPRINGBOOT_JVM_OPENSHIFT -> TestConfiguration.product() == ProductType.CAMEL_SPRINGBOOT
                && OpenshiftConfiguration.isOpenshift();
            default -> true;
        };
    }
}
