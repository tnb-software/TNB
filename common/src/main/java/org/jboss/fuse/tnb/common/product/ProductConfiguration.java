package org.jboss.fuse.tnb.common.product;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.QuarkusConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;

public enum ProductConfiguration {
    ALL, STANDALONE, QUARKUS_JVM, QUARKUS_JVM_LOCAL, QUARKUS_JVM_OPENSHIFT, QUARKUS_NATIVE, QUARKUS_NATIVE_LOCAL, QUARKUS_NATIVE_OPENSHIFT, CAMEL_K;

    public boolean isCurrentEnv() {
        switch (this) {
            case STANDALONE:
                return TestConfiguration.product() == ProductType.CAMEL_STANDALONE;
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
            default:
                return true;
        }
    }
}
