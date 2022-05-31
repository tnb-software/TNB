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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.KubernetesResource;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ready",
    "starting",
    "stopped"
})
@Generated("jsonschema2pojo")
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class PodStatus implements KubernetesResource {

    @JsonProperty("ready")
    private List<String> ready = new ArrayList<String>();
    @JsonProperty("starting")
    private List<String> starting = new ArrayList<String>();
    @JsonProperty("stopped")
    private List<String> stopped = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ready")
    public List<String> getReady() {
        return ready;
    }

    @JsonProperty("ready")
    public void setReady(List<String> ready) {
        this.ready = ready;
    }

    public PodStatus withReady(List<String> ready) {
        this.ready = ready;
        return this;
    }

    @JsonProperty("starting")
    public List<String> getStarting() {
        return starting;
    }

    @JsonProperty("starting")
    public void setStarting(List<String> starting) {
        this.starting = starting;
    }

    public PodStatus withStarting(List<String> starting) {
        this.starting = starting;
        return this;
    }

    @JsonProperty("stopped")
    public List<String> getStopped() {
        return stopped;
    }

    @JsonProperty("stopped")
    public void setStopped(List<String> stopped) {
        this.stopped = stopped;
    }

    public PodStatus withStopped(List<String> stopped) {
        this.stopped = stopped;
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

    public PodStatus withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PodStatus.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("ready");
        sb.append('=');
        sb.append(((this.ready == null) ? "<null>" : this.ready));
        sb.append(',');
        sb.append("starting");
        sb.append('=');
        sb.append(((this.starting == null) ? "<null>" : this.starting));
        sb.append(',');
        sb.append("stopped");
        sb.append('=');
        sb.append(((this.stopped == null) ? "<null>" : this.stopped));
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
        result = ((result * 31) + ((this.stopped == null) ? 0 : this.stopped.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        result = ((result * 31) + ((this.starting == null) ? 0 : this.starting.hashCode()));
        result = ((result * 31) + ((this.ready == null) ? 0 : this.ready.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PodStatus) == false) {
            return false;
        }
        PodStatus rhs = ((PodStatus) other);
        return (((((this.stopped == rhs.stopped) || ((this.stopped != null) && this.stopped.equals(rhs.stopped))) && (
            (this.additionalProperties == rhs.additionalProperties) || ((this.additionalProperties != null) && this.additionalProperties
                .equals(rhs.additionalProperties)))) && ((this.starting == rhs.starting) || ((this.starting != null) && this.starting
            .equals(rhs.starting)))) && ((this.ready == rhs.ready) || ((this.ready != null) && this.ready.equals(rhs.ready))));
    }
}
