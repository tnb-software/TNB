package generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.scmprovider;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"accessTokenRef","allBranches","api","organization","teamProject"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class AzureDevOps implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("accessTokenRef")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.scmprovider.azuredevops.AccessTokenRef accessTokenRef;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.scmprovider.azuredevops.AccessTokenRef getAccessTokenRef() {
        return accessTokenRef;
    }

    public void setAccessTokenRef(generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.scmprovider.azuredevops.AccessTokenRef accessTokenRef) {
        this.accessTokenRef = accessTokenRef;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("allBranches")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean allBranches;

    public Boolean getAllBranches() {
        return allBranches;
    }

    public void setAllBranches(Boolean allBranches) {
        this.allBranches = allBranches;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("api")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String api;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("organization")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String organization;

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("teamProject")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String teamProject;

    public String getTeamProject() {
        return teamProject;
    }

    public void setTeamProject(String teamProject) {
        this.teamProject = teamProject;
    }
}

