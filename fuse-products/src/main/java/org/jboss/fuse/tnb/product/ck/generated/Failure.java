package org.jboss.fuse.tnb.product.ck.generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.KubernetesResource;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reason",
    "recovery",
    "time"
})
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class Failure implements KubernetesResource {

    /**
     *
     */
    @JsonProperty("reason")
    @JsonPropertyDescription("")
    private String reason;
    /**
     *
     */
    @JsonProperty("recovery")
    @JsonPropertyDescription("")
    private Recovery recovery;
    /**
     *
     */
    @JsonProperty("time")
    @JsonPropertyDescription("")
    private String time;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public Failure() {
    }

    /**
     * @param reason
     * @param recovery
     * @param time
     */
    public Failure(String reason, Recovery recovery, String time) {
        super();
        this.reason = reason;
        this.recovery = recovery;
        this.time = time;
    }

    /**
     *
     */
    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    /**
     *
     */
    @JsonProperty("reason")
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     *
     */
    @JsonProperty("recovery")
    public Recovery getRecovery() {
        return recovery;
    }

    /**
     *
     */
    @JsonProperty("recovery")
    public void setRecovery(Recovery recovery) {
        this.recovery = recovery;
    }

    /**
     *
     */
    @JsonProperty("time")
    public String getTime() {
        return time;
    }

    /**
     *
     */
    @JsonProperty("time")
    public void setTime(String time) {
        this.time = time;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
