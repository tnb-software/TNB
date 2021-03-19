
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

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "apiVersion",
    "kind",
    "metadata",
    "spec",
    "status"
})
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class Integration extends CustomResource implements Namespaced {

    /**
     *
     */
    @JsonProperty("apiVersion")
    @JsonPropertyDescription("")
    private String apiVersion = "camel.apache.org/v1";
    /**
     *
     */
    @JsonProperty("kind")
    @JsonPropertyDescription("")
    private String kind = "Integration";
    /**
     *
     */
    @JsonProperty("metadata")
    @JsonPropertyDescription("")
    private ObjectMeta metadata;
    /**
     *
     */
    @JsonProperty("spec")
    @JsonPropertyDescription("")
    private IntegrationSpec spec;
    /**
     *
     */
    @JsonProperty("status")
    @JsonPropertyDescription("")
    private IntegrationStatus status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public Integration() {
    }

    /**
     * @param metadata
     * @param apiVersion
     * @param kind
     * @param spec
     * @param status
     */
    public Integration(String apiVersion, String kind, ObjectMeta metadata, IntegrationSpec spec, IntegrationStatus status) {
        super();
        this.apiVersion = apiVersion;
        this.kind = kind;
        this.metadata = metadata;
        this.spec = spec;
        this.status = status;
    }

    /**
     *
     */
    @JsonProperty("apiVersion")
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     *
     */
    @JsonProperty("apiVersion")
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     *
     */
    @JsonProperty("kind")
    public String getKind() {
        return kind;
    }

    /**
     *
     */
    @JsonProperty("kind")
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     *
     */
    @JsonProperty("metadata")
    public ObjectMeta getMetadata() {
        return metadata;
    }

    /**
     *
     */
    @JsonProperty("metadata")
    public void setMetadata(ObjectMeta metadata) {
        this.metadata = metadata;
    }

    /**
     *
     */
    @JsonProperty("spec")
    public IntegrationSpec getSpec() {
        return spec;
    }

    /**
     *
     */
    @JsonProperty("spec")
    public void setSpec(IntegrationSpec spec) {
        this.spec = spec;
    }

    /**
     *
     */
    @JsonProperty("status")
    public IntegrationStatus getStatus() {
        return status;
    }

    /**
     *
     */
    @JsonProperty("status")
    public void setStatus(IntegrationStatus status) {
        this.status = status;
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
