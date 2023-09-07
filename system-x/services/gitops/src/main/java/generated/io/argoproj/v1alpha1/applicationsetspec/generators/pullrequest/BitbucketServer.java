package generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"api","basicAuth","project","repo"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class BitbucketServer implements io.fabric8.kubernetes.api.model.KubernetesResource {

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

    @com.fasterxml.jackson.annotation.JsonProperty("basicAuth")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest.bitbucketserver.BasicAuth basicAuth;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest.bitbucketserver.BasicAuth getBasicAuth() {
        return basicAuth;
    }

    public void setBasicAuth(generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest.bitbucketserver.BasicAuth basicAuth) {
        this.basicAuth = basicAuth;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("project")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String project;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
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

