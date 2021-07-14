package org.jboss.fuse.tnb.amq.service.openshift.generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.processing.Generated;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.KubernetesResource;

/**
 * Configuration for the embedded web console
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "expose",
    "sslEnabled",
    "sslSecret",
    "useClientAuth"
})
@Generated("jsonschema2pojo")
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class Console implements KubernetesResource {

    /**
     * Whether or not to expose this port
     */
    @JsonProperty("expose")
    @JsonPropertyDescription("Whether or not to expose this port")
    private Boolean expose;
    /**
     * Whether or not to enable SSL on this port
     */
    @JsonProperty("sslEnabled")
    @JsonPropertyDescription("Whether or not to enable SSL on this port")
    private Boolean sslEnabled;
    /**
     * Name of the secret to use for ssl information
     */
    @JsonProperty("sslSecret")
    @JsonPropertyDescription("Name of the secret to use for ssl information")
    private String sslSecret;
    /**
     * If the embedded server requires client authentication
     */
    @JsonProperty("useClientAuth")
    @JsonPropertyDescription("If the embedded server requires client authentication")
    private Boolean useClientAuth;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Whether or not to expose this port
     */
    @JsonProperty("expose")
    public Boolean getExpose() {
        return expose;
    }

    /**
     * Whether or not to expose this port
     */
    @JsonProperty("expose")
    public void setExpose(Boolean expose) {
        this.expose = expose;
    }

    public Console withExpose(Boolean expose) {
        this.expose = expose;
        return this;
    }

    /**
     * Whether or not to enable SSL on this port
     */
    @JsonProperty("sslEnabled")
    public Boolean getSslEnabled() {
        return sslEnabled;
    }

    /**
     * Whether or not to enable SSL on this port
     */
    @JsonProperty("sslEnabled")
    public void setSslEnabled(Boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public Console withSslEnabled(Boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
        return this;
    }

    /**
     * Name of the secret to use for ssl information
     */
    @JsonProperty("sslSecret")
    public String getSslSecret() {
        return sslSecret;
    }

    /**
     * Name of the secret to use for ssl information
     */
    @JsonProperty("sslSecret")
    public void setSslSecret(String sslSecret) {
        this.sslSecret = sslSecret;
    }

    public Console withSslSecret(String sslSecret) {
        this.sslSecret = sslSecret;
        return this;
    }

    /**
     * If the embedded server requires client authentication
     */
    @JsonProperty("useClientAuth")
    public Boolean getUseClientAuth() {
        return useClientAuth;
    }

    /**
     * If the embedded server requires client authentication
     */
    @JsonProperty("useClientAuth")
    public void setUseClientAuth(Boolean useClientAuth) {
        this.useClientAuth = useClientAuth;
    }

    public Console withUseClientAuth(Boolean useClientAuth) {
        this.useClientAuth = useClientAuth;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Console withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Console.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("expose");
        sb.append('=');
        sb.append(((this.expose == null) ? "<null>" : this.expose));
        sb.append(',');
        sb.append("sslEnabled");
        sb.append('=');
        sb.append(((this.sslEnabled == null) ? "<null>" : this.sslEnabled));
        sb.append(',');
        sb.append("sslSecret");
        sb.append('=');
        sb.append(((this.sslSecret == null) ? "<null>" : this.sslSecret));
        sb.append(',');
        sb.append("useClientAuth");
        sb.append('=');
        sb.append(((this.useClientAuth == null) ? "<null>" : this.useClientAuth));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null) ? "<null>" : this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((this.sslSecret == null) ? 0 : this.sslSecret.hashCode()));
        result = ((result * 31) + ((this.useClientAuth == null) ? 0 : this.useClientAuth.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        result = ((result * 31) + ((this.expose == null) ? 0 : this.expose.hashCode()));
        result = ((result * 31) + ((this.sslEnabled == null) ? 0 : this.sslEnabled.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Console) == false) {
            return false;
        }
        Console rhs = ((Console) other);
        return ((((((this.sslSecret == rhs.sslSecret) || ((this.sslSecret != null) && this.sslSecret.equals(rhs.sslSecret))) && (
            (this.useClientAuth == rhs.useClientAuth) || ((this.useClientAuth != null) && this.useClientAuth.equals(rhs.useClientAuth)))) && (
            (this.additionalProperties == rhs.additionalProperties) || ((this.additionalProperties != null) && this.additionalProperties
                .equals(rhs.additionalProperties)))) && ((this.expose == rhs.expose) || ((this.expose != null) && this.expose.equals(rhs.expose))))
            && ((this.sslEnabled == rhs.sslEnabled) || ((this.sslEnabled != null) && this.sslEnabled.equals(rhs.sslEnabled))));
    }
}
