package generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"azuredevops","bitbucket","bitbucketServer","filters","gitea","github","gitlab","requeueAfterSeconds","template"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class PullRequest implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("azuredevops")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Azuredevops azuredevops;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Azuredevops getAzuredevops() {
        return azuredevops;
    }

    public void setAzuredevops(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Azuredevops azuredevops) {
        this.azuredevops = azuredevops;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("bitbucket")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Bitbucket bitbucket;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Bitbucket getBitbucket() {
        return bitbucket;
    }

    public void setBitbucket(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Bitbucket bitbucket) {
        this.bitbucket = bitbucket;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("bitbucketServer")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.BitbucketServer bitbucketServer;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.BitbucketServer getBitbucketServer() {
        return bitbucketServer;
    }

    public void setBitbucketServer(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.BitbucketServer bitbucketServer) {
        this.bitbucketServer = bitbucketServer;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("filters")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Filters> filters;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Filters> getFilters() {
        return filters;
    }

    public void setFilters(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Filters> filters) {
        this.filters = filters;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("gitea")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Gitea gitea;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Gitea getGitea() {
        return gitea;
    }

    public void setGitea(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Gitea gitea) {
        this.gitea = gitea;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("github")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Github github;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Github getGithub() {
        return github;
    }

    public void setGithub(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Github github) {
        this.github = github;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("gitlab")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Gitlab gitlab;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Gitlab getGitlab() {
        return gitlab;
    }

    public void setGitlab(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Gitlab gitlab) {
        this.gitlab = gitlab;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("requeueAfterSeconds")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Long requeueAfterSeconds;

    public Long getRequeueAfterSeconds() {
        return requeueAfterSeconds;
    }

    public void setRequeueAfterSeconds(Long requeueAfterSeconds) {
        this.requeueAfterSeconds = requeueAfterSeconds;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("template")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Template template;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Template getTemplate() {
        return template;
    }

    public void setTemplate(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.Template template) {
        this.template = template;
    }
}

