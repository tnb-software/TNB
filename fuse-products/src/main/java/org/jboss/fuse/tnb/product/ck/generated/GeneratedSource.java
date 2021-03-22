
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "interceptors",
    "language",
    "loader",
    "name",
    "property-names",
    "type"
})
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class GeneratedSource implements KubernetesResource
{

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
    @JsonProperty("interceptors")
    @JsonPropertyDescription("")
    private List<String> interceptors = new ArrayList<String>();
    /**
     * 
     */
    @JsonProperty("language")
    @JsonPropertyDescription("")
    private String language;
    /**
     * 
     */
    @JsonProperty("loader")
    @JsonPropertyDescription("")
    private String loader;
    /**
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("")
    private String name;
    /**
     * 
     */
    @JsonProperty("property-names")
    @JsonPropertyDescription("")
    private List<String> propertyNames = new ArrayList<String>();
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
     * 
     */
    public GeneratedSource() {
    }

    /**
     * 
     * @param contentKey
     * @param loader
     * @param propertyNames
     * @param name
     * @param language
     * @param compression
     * @param type
     * @param content
     * @param contentRef
     * @param interceptors
     */
    public GeneratedSource(Boolean compression, String content, String contentKey, String contentRef, List<String> interceptors, String language, String loader, String name, List<String> propertyNames, String type) {
        super();
        this.compression = compression;
        this.content = content;
        this.contentKey = contentKey;
        this.contentRef = contentRef;
        this.interceptors = interceptors;
        this.language = language;
        this.loader = loader;
        this.name = name;
        this.propertyNames = propertyNames;
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
    @JsonProperty("interceptors")
    public List<String> getInterceptors() {
        return interceptors;
    }

    /**
     * 
     */
    @JsonProperty("interceptors")
    public void setInterceptors(List<String> interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * 
     */
    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    /**
     * 
     */
    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 
     */
    @JsonProperty("loader")
    public String getLoader() {
        return loader;
    }

    /**
     * 
     */
    @JsonProperty("loader")
    public void setLoader(String loader) {
        this.loader = loader;
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
    @JsonProperty("property-names")
    public List<String> getPropertyNames() {
        return propertyNames;
    }

    /**
     * 
     */
    @JsonProperty("property-names")
    public void setPropertyNames(List<String> propertyNames) {
        this.propertyNames = propertyNames;
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
