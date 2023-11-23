package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"expose","sslEnabled","sslSecret","useClientAuth"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class Console implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Whether or not to expose this port
     */
    @com.fasterxml.jackson.annotation.JsonProperty("expose")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Whether or not to expose this port")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean expose;

    public Boolean getExpose() {
        return expose;
    }

    public void setExpose(Boolean expose) {
        this.expose = expose;
    }

    /**
     * Whether or not to enable SSL on this port
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sslEnabled")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Whether or not to enable SSL on this port")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean sslEnabled;

    public Boolean getSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(Boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    /**
     * Name of the secret to use for ssl information
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sslSecret")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Name of the secret to use for ssl information")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String sslSecret;

    public String getSslSecret() {
        return sslSecret;
    }

    public void setSslSecret(String sslSecret) {
        this.sslSecret = sslSecret;
    }

    /**
     * If the embedded server requires client authentication
     */
    @com.fasterxml.jackson.annotation.JsonProperty("useClientAuth")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("If the embedded server requires client authentication")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean useClientAuth;

    public Boolean getUseClientAuth() {
        return useClientAuth;
    }

    public void setUseClientAuth(Boolean useClientAuth) {
        this.useClientAuth = useClientAuth;
    }
}

