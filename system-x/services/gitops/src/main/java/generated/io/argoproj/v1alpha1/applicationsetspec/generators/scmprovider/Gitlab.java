package generated.io.argoproj.v1alpha1.applicationsetspec.generators.scmprovider;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"allBranches","api","group","includeSharedProjects","includeSubgroups","insecure","tokenRef","topic"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Gitlab implements io.fabric8.kubernetes.api.model.KubernetesResource {

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

    @com.fasterxml.jackson.annotation.JsonProperty("group")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("includeSharedProjects")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean includeSharedProjects;

    public Boolean getIncludeSharedProjects() {
        return includeSharedProjects;
    }

    public void setIncludeSharedProjects(Boolean includeSharedProjects) {
        this.includeSharedProjects = includeSharedProjects;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("includeSubgroups")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean includeSubgroups;

    public Boolean getIncludeSubgroups() {
        return includeSubgroups;
    }

    public void setIncludeSubgroups(Boolean includeSubgroups) {
        this.includeSubgroups = includeSubgroups;
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

    @com.fasterxml.jackson.annotation.JsonProperty("tokenRef")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.scmprovider.gitlab.TokenRef tokenRef;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.scmprovider.gitlab.TokenRef getTokenRef() {
        return tokenRef;
    }

    public void setTokenRef(generated.io.argoproj.v1alpha1.applicationsetspec.generators.scmprovider.gitlab.TokenRef tokenRef) {
        this.tokenRef = tokenRef;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("topic")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}

