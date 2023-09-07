package generated.io.argoproj.v1alpha1.applicationsetspec.strategy.rollingsync;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"matchExpressions","maxUpdate"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Steps implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("matchExpressions")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.strategy.rollingsync.steps.MatchExpressions> matchExpressions;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.strategy.rollingsync.steps.MatchExpressions> getMatchExpressions() {
        return matchExpressions;
    }

    public void setMatchExpressions(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.strategy.rollingsync.steps.MatchExpressions> matchExpressions) {
        this.matchExpressions = matchExpressions;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("maxUpdate")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private io.fabric8.kubernetes.api.model.IntOrString maxUpdate;

    public io.fabric8.kubernetes.api.model.IntOrString getMaxUpdate() {
        return maxUpdate;
    }

    public void setMaxUpdate(io.fabric8.kubernetes.api.model.IntOrString maxUpdate) {
        this.maxUpdate = maxUpdate;
    }
}

