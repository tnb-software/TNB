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
    "compression",
    "content",
    "contentKey",
    "contentRef",
    "mountPath",
    "name",
    "type"
})
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class Resource implements KubernetesResource {

    /**
     *
     */
    @JsonProperty("compression")
    @JsonPropertyDescription("")
    private Boolean compression;
    /**
     *
     */
    @JsonProperty("content")
    @JsonPropertyDescription("")
    private String content;
    /**
     *
     */
    @JsonProperty("contentKey")
    @JsonPropertyDescription("")
    private String contentKey;
    /**
     *
     */
    @JsonProperty("contentRef")
    @JsonPropertyDescription("")
    private String contentRef;
    /**
     *
     */
    @JsonProperty("mountPath")
    @JsonPropertyDescription("")
    private String mountPath;
    /**
     *
     */
    @JsonProperty("name")
    @JsonPropertyDescription("")
    private String name;
    /**
     *
     */
    @JsonProperty("type")
    @JsonPropertyDescription("")
    private String type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public Resource() {
    }

    /**
     * @param contentKey
     * @param mountPath
     * @param name
     * @param compression
     * @param type
     * @param content
     * @param contentRef
     */
    public Resource(Boolean compression, String content, String contentKey, String contentRef, String mountPath, String name, String type) {
        super();
        this.compression = compression;
        this.content = content;
        this.contentKey = contentKey;
        this.contentRef = contentRef;
        this.mountPath = mountPath;
        this.name = name;
        this.type = type;
    }

    /**
     *
     */
    @JsonProperty("compression")
    public Boolean getCompression() {
        return compression;
    }

    /**
     *
     */
    @JsonProperty("compression")
    public void setCompression(Boolean compression) {
        this.compression = compression;
    }

    /**
     *
     */
    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    /**
     *
     */
    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     */
    @JsonProperty("contentKey")
    public String getContentKey() {
        return contentKey;
    }

    /**
     *
     */
    @JsonProperty("contentKey")
    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    /**
     *
     */
    @JsonProperty("contentRef")
    public String getContentRef() {
        return contentRef;
    }

    /**
     *
     */
    @JsonProperty("contentRef")
    public void setContentRef(String contentRef) {
        this.contentRef = contentRef;
    }

    /**
     *
     */
    @JsonProperty("mountPath")
    public String getMountPath() {
        return mountPath;
    }

    /**
     *
     */
    @JsonProperty("mountPath")
    public void setMountPath(String mountPath) {
        this.mountPath = mountPath;
    }

    /**
     *
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
