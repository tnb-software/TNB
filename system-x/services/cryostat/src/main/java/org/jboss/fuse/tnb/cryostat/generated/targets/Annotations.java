package org.jboss.fuse.tnb.cryostat.generated.targets;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "platform",
    "cryostat"
})
@Generated("jsonschema2pojo")
public class Annotations {

    @JsonProperty("platform")
    private Map<String, Object> platform;
    @JsonProperty("cryostat")
    private Cryostat cryostat;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("platform")
    public Map<String, Object> getPlatform() {
        return platform;
    }

    @JsonProperty("platform")
    public void setPlatform(Map<String, Object> platform) {
        this.platform = platform;
    }

    @JsonProperty("cryostat")
    public Cryostat getCryostat() {
        return cryostat;
    }

    @JsonProperty("cryostat")
    public void setCryostat(Cryostat cryostat) {
        this.cryostat = cryostat;
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
