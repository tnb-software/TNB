package generated.io.argoproj.v1alpha1.applicationspec.sources;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"exclude","include","jsonnet","recurse"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Directory implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Exclude contains a glob pattern to match paths against that should be explicitly excluded from being used during manifest generation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("exclude")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Exclude contains a glob pattern to match paths against that should be explicitly excluded from being used during manifest generation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String exclude;

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    /**
     * Include contains a glob pattern to match paths against that should be explicitly included during manifest generation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("include")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Include contains a glob pattern to match paths against that should be explicitly included during manifest generation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String include;

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    /**
     * Jsonnet holds options specific to Jsonnet
     */
    @com.fasterxml.jackson.annotation.JsonProperty("jsonnet")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Jsonnet holds options specific to Jsonnet")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationspec.sources.directory.Jsonnet jsonnet;

    public generated.io.argoproj.v1alpha1.applicationspec.sources.directory.Jsonnet getJsonnet() {
        return jsonnet;
    }

    public void setJsonnet(generated.io.argoproj.v1alpha1.applicationspec.sources.directory.Jsonnet jsonnet) {
        this.jsonnet = jsonnet;
    }

    /**
     * Recurse specifies whether to scan a directory recursively for manifests
     */
    @com.fasterxml.jackson.annotation.JsonProperty("recurse")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Recurse specifies whether to scan a directory recursively for manifests")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean recurse;

    public Boolean getRecurse() {
        return recurse;
    }

    public void setRecurse(Boolean recurse) {
        this.recurse = recurse;
    }
}

