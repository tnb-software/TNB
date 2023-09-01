package generated.io.argoproj.v1alpha1.applicationstatus.history;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"chart","directory","helm","kustomize","path","plugin","ref","repoURL","targetRevision"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Source implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Chart is a Helm chart name, and must be specified for applications sourced from a Helm repo.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("chart")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Chart is a Helm chart name, and must be specified for applications sourced from a Helm repo.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String chart;

    public String getChart() {
        return chart;
    }

    public void setChart(String chart) {
        this.chart = chart;
    }

    /**
     * Directory holds path/directory specific options
     */
    @com.fasterxml.jackson.annotation.JsonProperty("directory")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Directory holds path/directory specific options")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.history.source.Directory directory;

    public generated.io.argoproj.v1alpha1.applicationstatus.history.source.Directory getDirectory() {
        return directory;
    }

    public void setDirectory(generated.io.argoproj.v1alpha1.applicationstatus.history.source.Directory directory) {
        this.directory = directory;
    }

    /**
     * Helm holds helm specific options
     */
    @com.fasterxml.jackson.annotation.JsonProperty("helm")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Helm holds helm specific options")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.history.source.Helm helm;

    public generated.io.argoproj.v1alpha1.applicationstatus.history.source.Helm getHelm() {
        return helm;
    }

    public void setHelm(generated.io.argoproj.v1alpha1.applicationstatus.history.source.Helm helm) {
        this.helm = helm;
    }

    /**
     * Kustomize holds kustomize specific options
     */
    @com.fasterxml.jackson.annotation.JsonProperty("kustomize")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Kustomize holds kustomize specific options")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.history.source.Kustomize kustomize;

    public generated.io.argoproj.v1alpha1.applicationstatus.history.source.Kustomize getKustomize() {
        return kustomize;
    }

    public void setKustomize(generated.io.argoproj.v1alpha1.applicationstatus.history.source.Kustomize kustomize) {
        this.kustomize = kustomize;
    }

    /**
     * Path is a directory path within the Git repository, and is only valid for applications sourced from Git.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("path")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Path is a directory path within the Git repository, and is only valid for applications sourced from Git.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Plugin holds config management plugin specific options
     */
    @com.fasterxml.jackson.annotation.JsonProperty("plugin")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Plugin holds config management plugin specific options")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.history.source.Plugin plugin;

    public generated.io.argoproj.v1alpha1.applicationstatus.history.source.Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(generated.io.argoproj.v1alpha1.applicationstatus.history.source.Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Ref is reference to another source within sources field. This field will not be used if used with a `source` tag.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ref")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Ref is reference to another source within sources field. This field will not be used if used with a `source` tag.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String ref;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * RepoURL is the URL to the repository (Git or Helm) that contains the application manifests
     */
    @com.fasterxml.jackson.annotation.JsonProperty("repoURL")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("RepoURL is the URL to the repository (Git or Helm) that contains the application manifests")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String repoURL;

    public String getRepoURL() {
        return repoURL;
    }

    public void setRepoURL(String repoURL) {
        this.repoURL = repoURL;
    }

    /**
     * TargetRevision defines the revision of the source to sync the application to. In case of Git, this can be commit, tag, or branch. If omitted, will equal to HEAD. In case of Helm, this is a semver tag for the Chart's version.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("targetRevision")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("TargetRevision defines the revision of the source to sync the application to. In case of Git, this can be commit, tag, or branch. If omitted, will equal to HEAD. In case of Helm, this is a semver tag for the Chart's version.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String targetRevision;

    public String getTargetRevision() {
        return targetRevision;
    }

    public void setTargetRevision(String targetRevision) {
        this.targetRevision = targetRevision;
    }
}

