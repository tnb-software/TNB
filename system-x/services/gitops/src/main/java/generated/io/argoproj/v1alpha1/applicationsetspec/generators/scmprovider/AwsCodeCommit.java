package generated.io.argoproj.v1alpha1.applicationsetspec.generators.scmprovider;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"allBranches","region","role","tagFilters"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class AwsCodeCommit implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("allBranches")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean allBranches;

    public Boolean getAllBranches() {
        return allBranches;
    }

    public void setAllBranches(Boolean allBranches) {
        this.allBranches = allBranches;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("region")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String region;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("role")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("tagFilters")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.scmprovider.awscodecommit.TagFilters> tagFilters;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.scmprovider.awscodecommit.TagFilters> getTagFilters() {
        return tagFilters;
    }

    public void setTagFilters(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.scmprovider.awscodecommit.TagFilters> tagFilters) {
        this.tagFilters = tagFilters;
    }
}

