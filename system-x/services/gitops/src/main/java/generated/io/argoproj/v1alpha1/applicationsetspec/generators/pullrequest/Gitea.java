package generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"api","insecure","owner","repo","tokenRef"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Gitea implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("api")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String api;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("insecure")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean insecure;

    public Boolean getInsecure() {
        return insecure;
    }

    public void setInsecure(Boolean insecure) {
        this.insecure = insecure;
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
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest.gitea.TokenRef tokenRef;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest.gitea.TokenRef getTokenRef() {
        return tokenRef;
    }

    public void setTokenRef(generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest.gitea.TokenRef tokenRef) {
        this.tokenRef = tokenRef;
    }
}

