package generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.template.spec.sources;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"env","name","parameters"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Plugin implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("env")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.template.spec.sources.plugin.Env> env;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.template.spec.sources.plugin.Env> getEnv() {
        return env;
    }

    public void setEnv(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.template.spec.sources.plugin.Env> env) {
        this.env = env;
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

    @com.fasterxml.jackson.annotation.JsonProperty("parameters")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.template.spec.sources.plugin.Parameters> parameters;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.template.spec.sources.plugin.Parameters> getParameters() {
        return parameters;
    }

    public void setParameters(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.pullrequest.template.spec.sources.plugin.Parameters> parameters) {
        this.parameters = parameters;
    }
}

