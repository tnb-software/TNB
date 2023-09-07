package generated.io.argoproj.v1alpha1.applicationsetspec.strategy;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"steps"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class RollingSync implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("steps")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.strategy.rollingsync.Steps> steps;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.strategy.rollingsync.Steps> getSteps() {
        return steps;
    }

    public void setSteps(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.strategy.rollingsync.Steps> steps) {
        this.steps = steps;
    }
}

