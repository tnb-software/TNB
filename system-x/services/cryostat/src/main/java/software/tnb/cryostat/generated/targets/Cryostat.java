package software.tnb.cryostat.generated.targets;

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
    "HOST",
    "PORT",
    "NAMESPACE",
    "POD_NAME"
})
@Generated("jsonschema2pojo")
public class Cryostat {

    @JsonProperty("HOST")
    private String host;
    @JsonProperty("PORT")
    private String port;
    @JsonProperty("NAMESPACE")
    private String namespace;
    @JsonProperty("POD_NAME")
    private String podName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("HOST")
    public String getHost() {
        return host;
    }

    @JsonProperty("HOST")
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty("PORT")
    public String getPort() {
        return port;
    }

    @JsonProperty("PORT")
    public void setPort(String port) {
        this.port = port;
    }

    @JsonProperty("NAMESPACE")
    public String getNamespace() {
        return namespace;
    }

    @JsonProperty("NAMESPACE")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @JsonProperty("POD_NAME")
    public String getPodName() {
        return podName;
    }

    @JsonProperty("POD_NAME")
    public void setPodName(String podName) {
        this.podName = podName;
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
