package generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"branchMatch","targetBranchMatch"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Filters implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("branchMatch")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String branchMatch;

    public String getBranchMatch() {
        return branchMatch;
    }

    public void setBranchMatch(String branchMatch) {
        this.branchMatch = branchMatch;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("targetBranchMatch")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String targetBranchMatch;

    public String getTargetBranchMatch() {
        return targetBranchMatch;
    }

    public void setTargetBranchMatch(String targetBranchMatch) {
        this.targetBranchMatch = targetBranchMatch;
    }
}

