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
    "type",
    "value"
})
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class Configuration implements KubernetesResource {

    /**
     *
     */
    @JsonProperty("type")
    @JsonPropertyDescription("")
    private String type;
    /**
     *
     */
    @JsonProperty("value")
    @JsonPropertyDescription("")
    private String value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public Configuration() {
    }

    /**
     * @param type
     * @param value
     */
    public Configuration(String type, String value) {
        super();
        this.type = type;
        this.value = value;
    }

    /**
     *
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     *
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     *
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
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
