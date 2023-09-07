package generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"configMapRef","input","requeueAfterSeconds","template","values"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Plugin implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("configMapRef")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.ConfigMapRef configMapRef;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.ConfigMapRef getConfigMapRef() {
        return configMapRef;
    }

    public void setConfigMapRef(generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.ConfigMapRef configMapRef) {
        this.configMapRef = configMapRef;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("input")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.Input input;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.Input getInput() {
        return input;
    }

    public void setInput(generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.Input input) {
        this.input = input;
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
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.Template template;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.Template getTemplate() {
        return template;
    }

    public void setTemplate(generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.plugin.Template template) {
        this.template = template;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("values")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.Map<java.lang.String, String> values;

    public java.util.Map<java.lang.String, String> getValues() {
        return values;
    }

    public void setValues(java.util.Map<java.lang.String, String> values) {
        this.values = values;
    }
}

