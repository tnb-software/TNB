package software.tnb.common.deployment;

import software.tnb.common.config.OpenshiftConfiguration;

public interface MicroshiftDeployable extends OpenshiftDeployable {

    @Override
    default boolean enabled() {
        return OpenshiftConfiguration.isMicroshift();
    }

    @Override
    default int priority() {
        return OpenshiftDeployable.super.priority() + 1;
    }
}
