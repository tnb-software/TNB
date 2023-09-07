package generated.io.argoproj.v1alpha1.applicationsetspec.generators.list;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"metadata","spec"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Template implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("metadata")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.list.template.Metadata metadata;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.list.template.Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(generated.io.argoproj.v1alpha1.applicationsetspec.generators.list.template.Metadata metadata) {
        this.metadata = metadata;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("spec")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.list.template.Spec spec;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.list.template.Spec getSpec() {
        return spec;
    }

    public void setSpec(generated.io.argoproj.v1alpha1.applicationsetspec.generators.list.template.Spec spec) {
        this.spec = spec;
    }
}

