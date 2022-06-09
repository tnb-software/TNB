package software.tnb.jms.amq.service.openshift.generated;

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
 * Specify the level of upgrade that should be allowed when an older product version is detected
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "enabled",
    "minor"
})
@Generated("jsonschema2pojo")
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class Upgrades implements KubernetesResource {

    /**
     * Set true to enable automatic micro version product upgrades, it is disabled by default.
     */
    @JsonProperty("enabled")
    @JsonPropertyDescription("Set true to enable automatic micro version product upgrades, it is disabled by default.")
    private Boolean enabled;
    /**
     * Set true to enable automatic minor product version upgrades, it is disabled by default. Requires spec.upgrades.enabled to be true.
     */
    @JsonProperty("minor")
    @JsonPropertyDescription("Set true to enable automatic minor product version upgrades, it is disabled by default. Requires spec.upgrades"
        + ".enabled to be true.")
    private Boolean minor;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Set true to enable automatic micro version product upgrades, it is disabled by default.
     */
    @JsonProperty("enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Set true to enable automatic micro version product upgrades, it is disabled by default.
     */
    @JsonProperty("enabled")
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Upgrades withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Set true to enable automatic minor product version upgrades, it is disabled by default. Requires spec.upgrades.enabled to be true.
     */
    @JsonProperty("minor")
    public Boolean getMinor() {
        return minor;
    }

    /**
     * Set true to enable automatic minor product version upgrades, it is disabled by default. Requires spec.upgrades.enabled to be true.
     */
    @JsonProperty("minor")
    public void setMinor(Boolean minor) {
        this.minor = minor;
    }

    public Upgrades withMinor(Boolean minor) {
        this.minor = minor;
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

    public Upgrades withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Upgrades.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("enabled");
        sb.append('=');
        sb.append(((this.enabled == null) ? "<null>" : this.enabled));
        sb.append(',');
        sb.append("minor");
        sb.append('=');
        sb.append(((this.minor == null) ? "<null>" : this.minor));
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
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        result = ((result * 31) + ((this.minor == null) ? 0 : this.minor.hashCode()));
        result = ((result * 31) + ((this.enabled == null) ? 0 : this.enabled.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Upgrades) == false) {
            return false;
        }
        Upgrades rhs = ((Upgrades) other);
        return ((((this.additionalProperties == rhs.additionalProperties) || ((this.additionalProperties != null) && this.additionalProperties
            .equals(rhs.additionalProperties))) && ((this.minor == rhs.minor) || ((this.minor != null) && this.minor.equals(rhs.minor)))) && (
            (this.enabled == rhs.enabled) || ((this.enabled != null) && this.enabled.equals(rhs.enabled))));
    }
}
