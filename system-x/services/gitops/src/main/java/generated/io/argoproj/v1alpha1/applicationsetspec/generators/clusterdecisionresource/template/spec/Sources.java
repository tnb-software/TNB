package generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"chart","directory","helm","kustomize","path","plugin","ref","repoURL","targetRevision"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Sources implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("chart")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String chart;

    public String getChart() {
        return chart;
    }

    public void setChart(String chart) {
        this.chart = chart;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("directory")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Directory directory;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Directory getDirectory() {
        return directory;
    }

    public void setDirectory(generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Directory directory) {
        this.directory = directory;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("helm")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Helm helm;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Helm getHelm() {
        return helm;
    }

    public void setHelm(generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Helm helm) {
        this.helm = helm;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("kustomize")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Kustomize kustomize;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Kustomize getKustomize() {
        return kustomize;
    }

    public void setKustomize(generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Kustomize kustomize) {
        this.kustomize = kustomize;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("path")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("plugin")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Plugin plugin;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.template.spec.sources.Plugin plugin) {
        this.plugin = plugin;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("ref")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String ref;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("repoURL")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String repoURL;

    public String getRepoURL() {
        return repoURL;
    }

    public void setRepoURL(String repoURL) {
        this.repoURL = repoURL;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("targetRevision")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String targetRevision;

    public String getTargetRevision() {
        return targetRevision;
    }

    public void setTargetRevision(String targetRevision) {
        this.targetRevision = targetRevision;
    }
}

