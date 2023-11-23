package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"configMaps","secrets"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class ExtraMounts implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Specifies ConfigMap names
     */
    @com.fasterxml.jackson.annotation.JsonProperty("configMaps")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies ConfigMap names")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> configMaps;

    public java.util.List<String> getConfigMaps() {
        return configMaps;
    }

    public void setConfigMaps(java.util.List<String> configMaps) {
        this.configMaps = configMaps;
    }

    /**
     * Specifies Secret names
     */
    @com.fasterxml.jackson.annotation.JsonProperty("secrets")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies Secret names")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> secrets;

    public java.util.List<String> getSecrets() {
        return secrets;
    }

    public void setSecrets(java.util.List<String> secrets) {
        this.secrets = secrets;
    }
}

