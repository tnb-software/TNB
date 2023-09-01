package generated.io.argoproj.v1alpha1.applicationspec.source;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"fileParameters","ignoreMissingValueFiles","parameters","passCredentials","releaseName","skipCrds","valueFiles","values","valuesObject","version"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Helm implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * FileParameters are file parameters to the helm template
     */
    @com.fasterxml.jackson.annotation.JsonProperty("fileParameters")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("FileParameters are file parameters to the helm template")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationspec.source.helm.FileParameters> fileParameters;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationspec.source.helm.FileParameters> getFileParameters() {
        return fileParameters;
    }

    public void setFileParameters(java.util.List<generated.io.argoproj.v1alpha1.applicationspec.source.helm.FileParameters> fileParameters) {
        this.fileParameters = fileParameters;
    }

    /**
     * IgnoreMissingValueFiles prevents helm template from failing when valueFiles do not exist locally by not appending them to helm template --values
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ignoreMissingValueFiles")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("IgnoreMissingValueFiles prevents helm template from failing when valueFiles do not exist locally by not appending them to helm template --values")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean ignoreMissingValueFiles;

    public Boolean getIgnoreMissingValueFiles() {
        return ignoreMissingValueFiles;
    }

    public void setIgnoreMissingValueFiles(Boolean ignoreMissingValueFiles) {
        this.ignoreMissingValueFiles = ignoreMissingValueFiles;
    }

    /**
     * Parameters is a list of Helm parameters which are passed to the helm template command upon manifest generation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("parameters")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Parameters is a list of Helm parameters which are passed to the helm template command upon manifest generation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationspec.source.helm.Parameters> parameters;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationspec.source.helm.Parameters> getParameters() {
        return parameters;
    }

    public void setParameters(java.util.List<generated.io.argoproj.v1alpha1.applicationspec.source.helm.Parameters> parameters) {
        this.parameters = parameters;
    }

    /**
     * PassCredentials pass credentials to all domains (Helm's --pass-credentials)
     */
    @com.fasterxml.jackson.annotation.JsonProperty("passCredentials")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("PassCredentials pass credentials to all domains (Helm's --pass-credentials)")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean passCredentials;

    public Boolean getPassCredentials() {
        return passCredentials;
    }

    public void setPassCredentials(Boolean passCredentials) {
        this.passCredentials = passCredentials;
    }

    /**
     * ReleaseName is the Helm release name to use. If omitted it will use the application name
     */
    @com.fasterxml.jackson.annotation.JsonProperty("releaseName")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ReleaseName is the Helm release name to use. If omitted it will use the application name")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String releaseName;

    public String getReleaseName() {
        return releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    /**
     * SkipCrds skips custom resource definition installation step (Helm's --skip-crds)
     */
    @com.fasterxml.jackson.annotation.JsonProperty("skipCrds")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("SkipCrds skips custom resource definition installation step (Helm's --skip-crds)")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean skipCrds;

    public Boolean getSkipCrds() {
        return skipCrds;
    }

    public void setSkipCrds(Boolean skipCrds) {
        this.skipCrds = skipCrds;
    }

    /**
     * ValuesFiles is a list of Helm value files to use when generating a template
     */
    @com.fasterxml.jackson.annotation.JsonProperty("valueFiles")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ValuesFiles is a list of Helm value files to use when generating a template")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> valueFiles;

    public java.util.List<String> getValueFiles() {
        return valueFiles;
    }

    public void setValueFiles(java.util.List<String> valueFiles) {
        this.valueFiles = valueFiles;
    }

    /**
     * Values specifies Helm values to be passed to helm template, typically defined as a block. ValuesObject takes precedence over Values, so use one or the other.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("values")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Values specifies Helm values to be passed to helm template, typically defined as a block. ValuesObject takes precedence over Values, so use one or the other.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String values;

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    /**
     * ValuesObject specifies Helm values to be passed to helm template, defined as a map. This takes precedence over Values.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("valuesObject")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ValuesObject specifies Helm values to be passed to helm template, defined as a map. This takes precedence over Values.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationspec.source.helm.ValuesObject valuesObject;

    public generated.io.argoproj.v1alpha1.applicationspec.source.helm.ValuesObject getValuesObject() {
        return valuesObject;
    }

    public void setValuesObject(generated.io.argoproj.v1alpha1.applicationspec.source.helm.ValuesObject valuesObject) {
        this.valuesObject = valuesObject;
    }

    /**
     * Version is the Helm version to use for templating ("3")
     */
    @com.fasterxml.jackson.annotation.JsonProperty("version")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Version is the Helm version to use for templating (\"3\")")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

