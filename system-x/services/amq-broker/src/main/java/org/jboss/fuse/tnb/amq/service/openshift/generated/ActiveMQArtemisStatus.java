package org.jboss.fuse.tnb.amq.service.openshift.generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.processing.Generated;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.KubernetesResource;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "podStatus"
})
@Generated("jsonschema2pojo")
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class ActiveMQArtemisStatus implements KubernetesResource {

    /**
     * (Required)
     */
    @JsonProperty("podStatus")
    private PodStatus podStatus;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * (Required)
     */
    @JsonProperty("podStatus")
    public PodStatus getPodStatus() {
        return podStatus;
    }

    /**
     * (Required)
     */
    @JsonProperty("podStatus")
    public void setPodStatus(PodStatus podStatus) {
        this.podStatus = podStatus;
    }

    public ActiveMQArtemisStatus withPodStatus(PodStatus podStatus) {
        this.podStatus = podStatus;
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

    public ActiveMQArtemisStatus withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ActiveMQArtemisStatus.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("podStatus");
        sb.append('=');
        sb.append(((this.podStatus == null) ? "<null>" : this.podStatus));
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
        result = ((result * 31) + ((this.podStatus == null) ? 0 : this.podStatus.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ActiveMQArtemisStatus) == false) {
            return false;
        }
        ActiveMQArtemisStatus rhs = ((ActiveMQArtemisStatus) other);
        return (((this.podStatus == rhs.podStatus) || ((this.podStatus != null) && this.podStatus.equals(rhs.podStatus))) && (
            (this.additionalProperties == rhs.additionalProperties) || ((this.additionalProperties != null) && this.additionalProperties
                .equals(rhs.additionalProperties))));
    }
}
