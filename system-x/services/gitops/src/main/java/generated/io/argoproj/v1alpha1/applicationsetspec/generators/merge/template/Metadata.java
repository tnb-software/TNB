package generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.template;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"annotations","finalizers","labels","name","namespace"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Metadata implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("annotations")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.Map<java.lang.String, String> annotations;

    public java.util.Map<java.lang.String, String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(java.util.Map<java.lang.String, String> annotations) {
        this.annotations = annotations;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("finalizers")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> finalizers;

    public java.util.List<String> getFinalizers() {
        return finalizers;
    }

    public void setFinalizers(java.util.List<String> finalizers) {
        this.finalizers = finalizers;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("labels")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.Map<java.lang.String, String> labels;

    public java.util.Map<java.lang.String, String> getLabels() {
        return labels;
    }

    public void setLabels(java.util.Map<java.lang.String, String> labels) {
        this.labels = labels;
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

    @com.fasterxml.jackson.annotation.JsonProperty("namespace")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String namespace;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}

