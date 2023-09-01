package generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"parameters"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Input implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("parameters")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.Map<java.lang.String, generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.input.Parameters> parameters;

    public java.util.Map<java.lang.String, generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.input.Parameters> getParameters() {
        return parameters;
    }

    public void setParameters(java.util.Map<java.lang.String, generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.input.Parameters> parameters) {
        this.parameters = parameters;
    }
}

