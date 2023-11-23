package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"enabled","minor"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class Upgrades implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Set true to enable automatic micro version product upgrades, it is disabled by default.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("enabled")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Set true to enable automatic micro version product upgrades, it is disabled by default.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Set true to enable automatic minor product version upgrades, it is disabled by default. Requires spec.upgrades.enabled to be true.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("minor")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Set true to enable automatic minor product version upgrades, it is disabled by default. Requires spec.upgrades.enabled to be true.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean minor;

    public Boolean getMinor() {
        return minor;
    }

    public void setMinor(Boolean minor) {
        this.minor = minor;
    }
}

