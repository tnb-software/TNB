package generated.io.argoproj.v1alpha1.applicationspec.syncpolicy.retry;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"duration","factor","maxDuration"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Backoff implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Duration is the amount to back off. Default unit is seconds, but could also be a duration (e.g. "2m", "1h")
     */
    @com.fasterxml.jackson.annotation.JsonProperty("duration")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Duration is the amount to back off. Default unit is seconds, but could also be a duration (e.g. \"2m\", \"1h\")")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Factor is a factor to multiply the base duration after each failed retry
     */
    @com.fasterxml.jackson.annotation.JsonProperty("factor")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Factor is a factor to multiply the base duration after each failed retry")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Long factor;

    public Long getFactor() {
        return factor;
    }

    public void setFactor(Long factor) {
        this.factor = factor;
    }

    /**
     * MaxDuration is the maximum amount of time allowed for the backoff strategy
     */
    @com.fasterxml.jackson.annotation.JsonProperty("maxDuration")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("MaxDuration is the maximum amount of time allowed for the backoff strategy")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String maxDuration;

    public String getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }
}

