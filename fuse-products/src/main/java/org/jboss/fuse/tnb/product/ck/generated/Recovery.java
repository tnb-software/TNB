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
    "attempt",
    "attemptMax",
    "attemptTime"
})
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class Recovery implements KubernetesResource {

    /**
     *
     */
    @JsonProperty("attempt")
    @JsonPropertyDescription("")
    private Integer attempt;
    /**
     *
     */
    @JsonProperty("attemptMax")
    @JsonPropertyDescription("")
    private Integer attemptMax;
    /**
     *
     */
    @JsonProperty("attemptTime")
    @JsonPropertyDescription("")
    private String attemptTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public Recovery() {
    }

    /**
     * @param attemptTime
     * @param attemptMax
     * @param attempt
     */
    public Recovery(Integer attempt, Integer attemptMax, String attemptTime) {
        super();
        this.attempt = attempt;
        this.attemptMax = attemptMax;
        this.attemptTime = attemptTime;
    }

    /**
     *
     */
    @JsonProperty("attempt")
    public Integer getAttempt() {
        return attempt;
    }

    /**
     *
     */
    @JsonProperty("attempt")
    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    /**
     *
     */
    @JsonProperty("attemptMax")
    public Integer getAttemptMax() {
        return attemptMax;
    }

    /**
     *
     */
    @JsonProperty("attemptMax")
    public void setAttemptMax(Integer attemptMax) {
        this.attemptMax = attemptMax;
    }

    /**
     *
     */
    @JsonProperty("attemptTime")
    public String getAttemptTime() {
        return attemptTime;
    }

    /**
     *
     */
    @JsonProperty("attemptTime")
    public void setAttemptTime(String attemptTime) {
        this.attemptTime = attemptTime;
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
