package generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider.template.spec.sources;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"commonAnnotations","commonAnnotationsEnvsubst","commonLabels","forceCommonAnnotations","forceCommonLabels","images","namePrefix","nameSuffix","namespace","replicas","version"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Kustomize implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("commonAnnotations")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.Map<java.lang.String, String> commonAnnotations;

    public java.util.Map<java.lang.String, String> getCommonAnnotations() {
        return commonAnnotations;
    }

    public void setCommonAnnotations(java.util.Map<java.lang.String, String> commonAnnotations) {
        this.commonAnnotations = commonAnnotations;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("commonAnnotationsEnvsubst")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean commonAnnotationsEnvsubst;

    public Boolean getCommonAnnotationsEnvsubst() {
        return commonAnnotationsEnvsubst;
    }

    public void setCommonAnnotationsEnvsubst(Boolean commonAnnotationsEnvsubst) {
        this.commonAnnotationsEnvsubst = commonAnnotationsEnvsubst;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("commonLabels")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.Map<java.lang.String, String> commonLabels;

    public java.util.Map<java.lang.String, String> getCommonLabels() {
        return commonLabels;
    }

    public void setCommonLabels(java.util.Map<java.lang.String, String> commonLabels) {
        this.commonLabels = commonLabels;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("forceCommonAnnotations")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean forceCommonAnnotations;

    public Boolean getForceCommonAnnotations() {
        return forceCommonAnnotations;
    }

    public void setForceCommonAnnotations(Boolean forceCommonAnnotations) {
        this.forceCommonAnnotations = forceCommonAnnotations;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("forceCommonLabels")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean forceCommonLabels;

    public Boolean getForceCommonLabels() {
        return forceCommonLabels;
    }

    public void setForceCommonLabels(Boolean forceCommonLabels) {
        this.forceCommonLabels = forceCommonLabels;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("images")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> images;

    public java.util.List<String> getImages() {
        return images;
    }

    public void setImages(java.util.List<String> images) {
        this.images = images;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("namePrefix")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String namePrefix;

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("nameSuffix")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String nameSuffix;

    public String getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("namespace")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String namespace;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("replicas")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider.template.spec.sources.kustomize.Replicas> replicas;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider.template.spec.sources.kustomize.Replicas> getReplicas() {
        return replicas;
    }

    public void setReplicas(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider.template.spec.sources.kustomize.Replicas> replicas) {
        this.replicas = replicas;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("version")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

