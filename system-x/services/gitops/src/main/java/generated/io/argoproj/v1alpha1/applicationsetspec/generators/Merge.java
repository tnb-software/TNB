package generated.io.argoproj.v1alpha1.applicationsetspec.generators;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"generators","mergeKeys","template"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Merge implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("generators")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.Generators> generators;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.Generators> getGenerators() {
        return generators;
    }

    public void setGenerators(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.Generators> generators) {
        this.generators = generators;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("mergeKeys")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> mergeKeys;

    public java.util.List<String> getMergeKeys() {
        return mergeKeys;
    }

    public void setMergeKeys(java.util.List<String> mergeKeys) {
        this.mergeKeys = mergeKeys;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("template")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.Template template;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.Template getTemplate() {
        return template;
    }

    public void setTemplate(generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.Template template) {
        this.template = template;
    }
}

