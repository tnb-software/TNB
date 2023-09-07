package generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.clusters.template.spec.source.directory;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"extVars","libs","tlas"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Jsonnet implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("extVars")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.clusters.template.spec.source.directory.jsonnet.ExtVars> extVars;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.clusters.template.spec.source.directory.jsonnet.ExtVars> getExtVars() {
        return extVars;
    }

    public void setExtVars(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.clusters.template.spec.source.directory.jsonnet.ExtVars> extVars) {
        this.extVars = extVars;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("libs")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> libs;

    public java.util.List<String> getLibs() {
        return libs;
    }

    public void setLibs(java.util.List<String> libs) {
        this.libs = libs;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("tlas")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.clusters.template.spec.source.directory.jsonnet.Tlas> tlas;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.clusters.template.spec.source.directory.jsonnet.Tlas> getTlas() {
        return tlas;
    }

    public void setTlas(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.clusters.template.spec.source.directory.jsonnet.Tlas> tlas) {
        this.tlas = tlas;
    }
}

