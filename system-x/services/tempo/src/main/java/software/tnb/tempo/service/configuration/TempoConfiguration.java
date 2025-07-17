package software.tnb.tempo.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class TempoConfiguration extends ServiceConfiguration {

    private static final String RESOURCE_LIMITS_CPU = "tempo.resource.limits.cpu";
    private static final String RESOURCE_LIMITS_MEMORY = "tempo.resource.limits.memory";

    public TempoConfiguration withResourceLimitsCpu(String cpu) {
        set(RESOURCE_LIMITS_CPU, cpu);
        return this;
    }

    public String getResourceLimitsCpu() {
        return get(RESOURCE_LIMITS_CPU, String.class);
    }

    public TempoConfiguration withResourceLimitsMemory(String memory) {
        set(RESOURCE_LIMITS_MEMORY, memory);
        return this;
    }

    public String getResourceLimitsMemory() {
        return get(RESOURCE_LIMITS_MEMORY, String.class);
    }
}
