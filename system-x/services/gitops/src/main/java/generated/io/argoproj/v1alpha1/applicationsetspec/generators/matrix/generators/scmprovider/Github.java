package generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"allBranches","api","appSecretName","organization","tokenRef"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Github implements io.fabric8.kubernetes.api.model.KubernetesResource {

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

    @com.fasterxml.jackson.annotation.JsonProperty("appSecretName")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String appSecretName;

    public String getAppSecretName() {
        return appSecretName;
    }

    public void setAppSecretName(String appSecretName) {
        this.appSecretName = appSecretName;
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

    @com.fasterxml.jackson.annotation.JsonProperty("tokenRef")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider.github.TokenRef tokenRef;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider.github.TokenRef getTokenRef() {
        return tokenRef;
    }

    public void setTokenRef(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider.github.TokenRef tokenRef) {
        this.tokenRef = tokenRef;
    }
}

