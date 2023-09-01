package generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.pullrequest;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"api","appSecretName","labels","owner","repo","tokenRef"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Github implements io.fabric8.kubernetes.api.model.KubernetesResource {

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

    @com.fasterxml.jackson.annotation.JsonProperty("labels")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> labels;

    public java.util.List<String> getLabels() {
        return labels;
    }

    public void setLabels(java.util.List<String> labels) {
        this.labels = labels;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("owner")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("repo")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String repo;

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("tokenRef")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.pullrequest.github.TokenRef tokenRef;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.pullrequest.github.TokenRef getTokenRef() {
        return tokenRef;
    }

    public void setTokenRef(generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.pullrequest.github.TokenRef tokenRef) {
        this.tokenRef = tokenRef;
    }
}

