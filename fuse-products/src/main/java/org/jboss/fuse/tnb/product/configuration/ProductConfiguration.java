package org.jboss.fuse.tnb.product.configuration;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.cq.configuration.QuarkusConfiguration;

public enum ProductConfiguration {
    ALL, QUARKUS_JVM, QUARKUS_JVM_LOCAL, QUARKUS_JVM_OPENSHIFT, QUARKUS_NATIVE, QUARKUS_NATIVE_LOCAL, QUARKUS_NATIVE_OPENSHIFT, CAMEL_K,
        SPRINGBOOT_JVM_LOCAL, SPRINGBOOT_JVM_OPENSHIFT;

    public boolean isCurrentEnv() {
        switch (this) {
            case QUARKUS_JVM:
                return TestConfiguration.product() == ProductType.CAMEL_QUARKUS && !QuarkusConfiguration.isQuarkusNative();
            case QUARKUS_JVM_LOCAL:
                return TestConfiguration.product() == ProductType.CAMEL_QUARKUS && !QuarkusConfiguration.isQuarkusNative() && !OpenshiftConfiguration
                    .isOpenshift();
            case QUARKUS_JVM_OPENSHIFT:
                return TestConfiguration.product() == ProductType.CAMEL_QUARKUS && !QuarkusConfiguration.isQuarkusNative() && OpenshiftConfiguration
                    .isOpenshift();
            case QUARKUS_NATIVE:
                return TestConfiguration.product() == ProductType.CAMEL_QUARKUS && QuarkusConfiguration.isQuarkusNative();
            case QUARKUS_NATIVE_LOCAL:
                return TestConfiguration.product() == ProductType.CAMEL_QUARKUS && QuarkusConfiguration.isQuarkusNative() && !OpenshiftConfiguration
                    .isOpenshift();
            case QUARKUS_NATIVE_OPENSHIFT:
                return TestConfiguration.product() == ProductType.CAMEL_QUARKUS && QuarkusConfiguration.isQuarkusNative() && OpenshiftConfiguration
                    .isOpenshift();
            case CAMEL_K:
                return TestConfiguration.product() == ProductType.CAMEL_K;
            case SPRINGBOOT_JVM_LOCAL:
                return TestConfiguration.product() == ProductType.CAMEL_SPRINGBOOT && !OpenshiftConfiguration.isOpenshift();
            case SPRINGBOOT_JVM_OPENSHIFT:
                return TestConfiguration.product() == ProductType.CAMEL_SPRINGBOOT && OpenshiftConfiguration.isOpenshift();
            default:
                return true;
        }
    }
}
