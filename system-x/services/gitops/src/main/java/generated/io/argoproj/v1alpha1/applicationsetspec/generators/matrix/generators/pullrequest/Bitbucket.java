package generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"api","basicAuth","bearerToken","owner","repo"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Bitbucket implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("api")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String api;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("basicAuth")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.bitbucket.BasicAuth basicAuth;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.bitbucket.BasicAuth getBasicAuth() {
        return basicAuth;
    }

    public void setBasicAuth(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.bitbucket.BasicAuth basicAuth) {
        this.basicAuth = basicAuth;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("bearerToken")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.bitbucket.BearerToken bearerToken;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.bitbucket.BearerToken getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.bitbucket.BearerToken bearerToken) {
        this.bearerToken = bearerToken;
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
}

