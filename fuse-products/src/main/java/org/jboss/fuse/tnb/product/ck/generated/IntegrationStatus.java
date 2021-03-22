
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
    "capabilities",
    "conditions",
    "configuration",
    "dependencies",
    "digest",
    "failure",
    "generatedResources",
    "generatedSources",
    "image",
    "kit",
    "phase",
    "platform",
    "profile",
    "replicas",
    "runtimeProvider",
    "runtimeVersion",
    "selector",
    "version"
})
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class IntegrationStatus implements KubernetesResource
{

    /**
     * 
     */
    @JsonProperty("capabilities")
    @JsonPropertyDescription("")
    private List<String> capabilities = new ArrayList<String>();
    /**
     * 
     */
    @JsonProperty("conditions")
    @JsonPropertyDescription("")
    private List<Condition> conditions = new ArrayList<Condition>();
    /**
     * 
     */
    @JsonProperty("configuration")
    @JsonPropertyDescription("")
    private List<Configuration> configuration = new ArrayList<Configuration>();
    /**
     * 
     */
    @JsonProperty("dependencies")
    @JsonPropertyDescription("")
    private List<String> dependencies = new ArrayList<String>();
    /**
     * 
     */
    @JsonProperty("digest")
    @JsonPropertyDescription("")
    private String digest;
    /**
     * 
     */
    @JsonProperty("failure")
    @JsonPropertyDescription("")
    private Failure failure;
    /**
     * 
     */
    @JsonProperty("generatedResources")
    @JsonPropertyDescription("")
    private List<GeneratedResource> generatedResources = new ArrayList<GeneratedResource>();
    /**
     * 
     */
    @JsonProperty("generatedSources")
    @JsonPropertyDescription("")
    private List<GeneratedSource> generatedSources = new ArrayList<GeneratedSource>();
    /**
     * 
     */
    @JsonProperty("image")
    @JsonPropertyDescription("")
    private String image;
    /**
     * 
     */
    @JsonProperty("kit")
    @JsonPropertyDescription("")
    private String kit;
    /**
     * 
     */
    @JsonProperty("phase")
    @JsonPropertyDescription("")
    private String phase;
    /**
     * 
     */
    @JsonProperty("platform")
    @JsonPropertyDescription("")
    private String platform;
    /**
     * 
     */
    @JsonProperty("profile")
    @JsonPropertyDescription("")
    private String profile;
    /**
     * 
     */
    @JsonProperty("replicas")
    @JsonPropertyDescription("")
    private Integer replicas;
    /**
     * 
     */
    @JsonProperty("runtimeProvider")
    @JsonPropertyDescription("")
    private String runtimeProvider;
    /**
     * 
     */
    @JsonProperty("runtimeVersion")
    @JsonPropertyDescription("")
    private String runtimeVersion;
    /**
     * 
     */
    @JsonProperty("selector")
    @JsonPropertyDescription("")
    private String selector;
    /**
     * 
     */
    @JsonProperty("version")
    @JsonPropertyDescription("")
    private String version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public IntegrationStatus() {
    }

    /**
     * 
     * @param phase
     * @param image
     * @param runtimeVersion
     * @param capabilities
     * @param configuration
     * @param replicas
     * @param profile
     * @param runtimeProvider
     * @param version
     * @param platform
     * @param dependencies
     * @param generatedResources
     * @param failure
     * @param kit
     * @param digest
     * @param selector
     * @param conditions
     * @param generatedSources
     */
    public IntegrationStatus(List<String> capabilities, List<Condition> conditions, List<Configuration> configuration, List<String> dependencies, String digest, Failure failure, List<GeneratedResource> generatedResources, List<GeneratedSource> generatedSources, String image, String kit, String phase, String platform, String profile, Integer replicas, String runtimeProvider, String runtimeVersion, String selector, String version) {
        super();
        this.capabilities = capabilities;
        this.conditions = conditions;
        this.configuration = configuration;
        this.dependencies = dependencies;
        this.digest = digest;
        this.failure = failure;
        this.generatedResources = generatedResources;
        this.generatedSources = generatedSources;
        this.image = image;
        this.kit = kit;
        this.phase = phase;
        this.platform = platform;
        this.profile = profile;
        this.replicas = replicas;
        this.runtimeProvider = runtimeProvider;
        this.runtimeVersion = runtimeVersion;
        this.selector = selector;
        this.version = version;
    }

    /**
     * 
     */
    @JsonProperty("capabilities")
    public List<String> getCapabilities() {
        return capabilities;
    }

    /**
     * 
     */
    @JsonProperty("capabilities")
    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * 
     */
    @JsonProperty("conditions")
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * 
     */
    @JsonProperty("conditions")
    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    /**
     * 
     */
    @JsonProperty("configuration")
    public List<Configuration> getConfiguration() {
        return configuration;
    }

    /**
     * 
     */
    @JsonProperty("configuration")
    public void setConfiguration(List<Configuration> configuration) {
        this.configuration = configuration;
    }

    /**
     * 
     */
    @JsonProperty("dependencies")
    public List<String> getDependencies() {
        return dependencies;
    }

    /**
     * 
     */
    @JsonProperty("dependencies")
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * 
     */
    @JsonProperty("digest")
    public String getDigest() {
        return digest;
    }

    /**
     * 
     */
    @JsonProperty("digest")
    public void setDigest(String digest) {
        this.digest = digest;
    }

    /**
     * 
     */
    @JsonProperty("failure")
    public Failure getFailure() {
        return failure;
    }

    /**
     * 
     */
    @JsonProperty("failure")
    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    /**
     * 
     */
    @JsonProperty("generatedResources")
    public List<GeneratedResource> getGeneratedResources() {
        return generatedResources;
    }

    /**
     * 
     */
    @JsonProperty("generatedResources")
    public void setGeneratedResources(List<GeneratedResource> generatedResources) {
        this.generatedResources = generatedResources;
    }

    /**
     * 
     */
    @JsonProperty("generatedSources")
    public List<GeneratedSource> getGeneratedSources() {
        return generatedSources;
    }

    /**
     * 
     */
    @JsonProperty("generatedSources")
    public void setGeneratedSources(List<GeneratedSource> generatedSources) {
        this.generatedSources = generatedSources;
    }

    /**
     * 
     */
    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    /**
     * 
     */
    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * 
     */
    @JsonProperty("kit")
    public String getKit() {
        return kit;
    }

    /**
     * 
     */
    @JsonProperty("kit")
    public void setKit(String kit) {
        this.kit = kit;
    }

    /**
     * 
     */
    @JsonProperty("phase")
    public String getPhase() {
        return phase;
    }

    /**
     * 
     */
    @JsonProperty("phase")
    public void setPhase(String phase) {
        this.phase = phase;
    }

    /**
     * 
     */
    @JsonProperty("platform")
    public String getPlatform() {
        return platform;
    }

    /**
     * 
     */
    @JsonProperty("platform")
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * 
     */
    @JsonProperty("profile")
    public String getProfile() {
        return profile;
    }

    /**
     * 
     */
    @JsonProperty("profile")
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * 
     */
    @JsonProperty("replicas")
    public Integer getReplicas() {
        return replicas;
    }

    /**
     * 
     */
    @JsonProperty("replicas")
    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    /**
     * 
     */
    @JsonProperty("runtimeProvider")
    public String getRuntimeProvider() {
        return runtimeProvider;
    }

    /**
     * 
     */
    @JsonProperty("runtimeProvider")
    public void setRuntimeProvider(String runtimeProvider) {
        this.runtimeProvider = runtimeProvider;
    }

    /**
     * 
     */
    @JsonProperty("runtimeVersion")
    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    /**
     * 
     */
    @JsonProperty("runtimeVersion")
    public void setRuntimeVersion(String runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
    }

    /**
     * 
     */
    @JsonProperty("selector")
    public String getSelector() {
        return selector;
    }

    /**
     * 
     */
    @JsonProperty("selector")
    public void setSelector(String selector) {
        this.selector = selector;
    }

    /**
     * 
     */
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    /**
     * 
     */
    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
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
