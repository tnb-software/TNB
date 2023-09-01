package generated.io.argoproj.v1alpha1.applicationsetspec.generators;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"generators","template"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Matrix implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("generators")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.Generators> generators;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.Generators> getGenerators() {
        return generators;
    }

    public void setGenerators(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.Generators> generators) {
        this.generators = generators;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("template")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.Template template;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.Template getTemplate() {
        return template;
    }

    public void setTemplate(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.Template template) {
        this.template = template;
    }
}

