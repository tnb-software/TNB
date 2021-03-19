
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
    "configuration",
    "dependencies",
    "flows",
    "kit",
    "profile",
    "replicas",
    "repositories",
    "resources",
    "serviceAccountName",
    "sources",
    "traits"
})
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class IntegrationSpec implements KubernetesResource
{

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
    @JsonProperty("flows")
    @JsonPropertyDescription("")
    private List<Flow> flows = new ArrayList<Flow>();
    /**
     * 
     */
    @JsonProperty("kit")
    @JsonPropertyDescription("")
    private String kit;
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
    @JsonProperty("repositories")
    @JsonPropertyDescription("")
    private List<String> repositories = new ArrayList<String>();
    /**
     * 
     */
    @JsonProperty("resources")
    @JsonPropertyDescription("")
    private List<Resource> resources = new ArrayList<Resource>();
    /**
     * 
     */
    @JsonProperty("serviceAccountName")
    @JsonPropertyDescription("")
    private String serviceAccountName;
    /**
     * 
     */
    @JsonProperty("sources")
    @JsonPropertyDescription("")
    private List<Source> sources = new ArrayList<Source>();
    /**
     * 
     */
    @JsonProperty("traits")
    @JsonPropertyDescription("")
    private Traits traits;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public IntegrationSpec() {
    }

    /**
     * 
     * @param traits
     * @param sources
     * @param configuration
     * @param flows
     * @param repositories
     * @param replicas
     * @param serviceAccountName
     * @param kit
     * @param profile
     * @param resources
     * @param dependencies
     */
    public IntegrationSpec(List<Configuration> configuration, List<String> dependencies, List<Flow> flows, String kit, String profile, Integer replicas, List<String> repositories, List<Resource> resources, String serviceAccountName, List<Source> sources, Traits traits) {
        super();
        this.configuration = configuration;
        this.dependencies = dependencies;
        this.flows = flows;
        this.kit = kit;
        this.profile = profile;
        this.replicas = replicas;
        this.repositories = repositories;
        this.resources = resources;
        this.serviceAccountName = serviceAccountName;
        this.sources = sources;
        this.traits = traits;
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
    @JsonProperty("flows")
    public List<Flow> getFlows() {
        return flows;
    }

    /**
     * 
     */
    @JsonProperty("flows")
    public void setFlows(List<Flow> flows) {
        this.flows = flows;
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
    @JsonProperty("repositories")
    public List<String> getRepositories() {
        return repositories;
    }

    /**
     * 
     */
    @JsonProperty("repositories")
    public void setRepositories(List<String> repositories) {
        this.repositories = repositories;
    }

    /**
     * 
     */
    @JsonProperty("resources")
    public List<Resource> getResources() {
        return resources;
    }

    /**
     * 
     */
    @JsonProperty("resources")
    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    /**
     * 
     */
    @JsonProperty("serviceAccountName")
    public String getServiceAccountName() {
        return serviceAccountName;
    }

    /**
     * 
     */
    @JsonProperty("serviceAccountName")
    public void setServiceAccountName(String serviceAccountName) {
        this.serviceAccountName = serviceAccountName;
    }

    /**
     * 
     */
    @JsonProperty("sources")
    public List<Source> getSources() {
        return sources;
    }

    /**
     * 
     */
    @JsonProperty("sources")
    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    /**
     * 
     */
    @JsonProperty("traits")
    public Traits getTraits() {
        return traits;
    }

    /**
     * 
     */
    @JsonProperty("traits")
    public void setTraits(Traits traits) {
        this.traits = traits;
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
