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
    "cpu",
    "memory"
})
@Generated("jsonschema2pojo")
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class Limits implements KubernetesResource {

    @JsonProperty("cpu")
    private String cpu;
    @JsonProperty("memory")
    private String memory;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cpu")
    public String getCpu() {
        return cpu;
    }

    @JsonProperty("cpu")
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public Limits withCpu(String cpu) {
        this.cpu = cpu;
        return this;
    }

    @JsonProperty("memory")
    public String getMemory() {
        return memory;
    }

    @JsonProperty("memory")
    public void setMemory(String memory) {
        this.memory = memory;
    }

    public Limits withMemory(String memory) {
        this.memory = memory;
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

    public Limits withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Limits.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("cpu");
        sb.append('=');
        sb.append(((this.cpu == null) ? "<null>" : this.cpu));
        sb.append(',');
        sb.append("memory");
        sb.append('=');
        sb.append(((this.memory == null) ? "<null>" : this.memory));
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
        result = ((result * 31) + ((this.cpu == null) ? 0 : this.cpu.hashCode()));
        result = ((result * 31) + ((this.memory == null) ? 0 : this.memory.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Limits) == false) {
            return false;
        }
        Limits rhs = ((Limits) other);
        return ((((this.cpu == rhs.cpu) || ((this.cpu != null) && this.cpu.equals(rhs.cpu))) && ((this.memory == rhs.memory) || ((this.memory != null)
            && this.memory.equals(rhs.memory)))) && ((this.additionalProperties == rhs.additionalProperties) || ((this.additionalProperties != null)
            && this.additionalProperties.equals(rhs.additionalProperties))));
    }
}
