package generated.io.argoproj.v1alpha1.applicationsetspec.generators;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"configMapRef","labelSelector","name","requeueAfterSeconds","template","values"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class ClusterDecisionResource implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("configMapRef")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String configMapRef;

    public String getConfigMapRef() {
        return configMapRef;
    }

    public void setConfigMapRef(String configMapRef) {
        this.configMapRef = configMapRef;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("labelSelector")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.LabelSelector labelSelector;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.LabelSelector getLabelSelector() {
        return labelSelector;
    }

    public void setLabelSelector(generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.LabelSelector labelSelector) {
        this.labelSelector = labelSelector;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.Template template;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.Template getTemplate() {
        return template;
    }

    public void setTemplate(generated.io.argoproj.v1alpha1.applicationsetspec.generators.clusterdecisionresource.Template template) {
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

