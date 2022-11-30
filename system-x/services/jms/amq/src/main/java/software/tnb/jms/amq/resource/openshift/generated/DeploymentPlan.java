package software.tnb.jms.amq.resource.openshift.generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.processing.Generated;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.KubernetesResource;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jolokiaAgentEnabled",
    "image",
    "initImage",
    "journalType",
    "managementRBACEnabled",
    "messageMigration",
    "persistenceEnabled",
    "requireLogin",
    "size",
    "storage",
    "resources"
})
@Generated("jsonschema2pojo")
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class DeploymentPlan implements KubernetesResource {

    /**
     * If true enable the Jolokia JVM Agent
     */
    @JsonProperty("jolokiaAgentEnabled")
    @JsonPropertyDescription("If true enable the Jolokia JVM Agent")
    private Boolean jolokiaAgentEnabled;
    /**
     * The image used for the broker deployment
     */
    @JsonProperty("image")
    @JsonPropertyDescription("The image used for the broker deployment")
    private String image;
    /**
     * The init container image used to configure broker
     */
    @JsonProperty("initImage")
    @JsonPropertyDescription("The init container image used to configure broker")
    private String initImage;
    /**
     * If aio use ASYNCIO, if nio use NIO for journal IO
     */
    @JsonProperty("journalType")
    @JsonPropertyDescription("If aio use ASYNCIO, if nio use NIO for journal IO")
    private String journalType;
    /**
     * If true enable the management role based access control
     */
    @JsonProperty("managementRBACEnabled")
    @JsonPropertyDescription("If true enable the management role based access control")
    private Boolean managementRBACEnabled;
    /**
     * If true migrate messages on scaledown
     */
    @JsonProperty("messageMigration")
    @JsonPropertyDescription("If true migrate messages on scaledown")
    private Boolean messageMigration;
    /**
     * If true use persistent volume via persistent volume claim for journal storage
     */
    @JsonProperty("persistenceEnabled")
    @JsonPropertyDescription("If true use persistent volume via persistent volume claim for journal storage")
    private Boolean persistenceEnabled;
    /**
     * If true require user password login credentials for broker protocol ports
     */
    @JsonProperty("requireLogin")
    @JsonPropertyDescription("If true require user password login credentials for broker protocol ports")
    private Boolean requireLogin;
    /**
     * The number of broker pods to deploy
     */
    @JsonProperty("size")
    @JsonPropertyDescription("The number of broker pods to deploy")
    private Integer size;
    /**
     * the storage capacity
     */
    @JsonProperty("storage")
    @JsonPropertyDescription("the storage capacity")
    private Storage storage;
    @JsonProperty("resources")
    private Resources resources;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * If true enable the Jolokia JVM Agent
     */
    @JsonProperty("jolokiaAgentEnabled")
    public Boolean getJolokiaAgentEnabled() {
        return jolokiaAgentEnabled;
    }

    /**
     * If true enable the Jolokia JVM Agent
     */
    @JsonProperty("jolokiaAgentEnabled")
    public void setJolokiaAgentEnabled(Boolean jolokiaAgentEnabled) {
        this.jolokiaAgentEnabled = jolokiaAgentEnabled;
    }

    public DeploymentPlan withJolokiaAgentEnabled(Boolean jolokiaAgentEnabled) {
        this.jolokiaAgentEnabled = jolokiaAgentEnabled;
        return this;
    }

    /**
     * The image used for the broker deployment
     */
    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    /**
     * The image used for the broker deployment
     */
    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    public DeploymentPlan withImage(String image) {
        this.image = image;
        return this;
    }

    /**
     * The init container image used to configure broker
     */
    @JsonProperty("initImage")
    public String getInitImage() {
        return initImage;
    }

    /**
     * The init container image used to configure broker
     */
    @JsonProperty("initImage")
    public void setInitImage(String initImage) {
        this.initImage = initImage;
    }

    public DeploymentPlan withInitImage(String initImage) {
        this.initImage = initImage;
        return this;
    }

    /**
     * If aio use ASYNCIO, if nio use NIO for journal IO
     */
    @JsonProperty("journalType")
    public String getJournalType() {
        return journalType;
    }

    /**
     * If aio use ASYNCIO, if nio use NIO for journal IO
     */
    @JsonProperty("journalType")
    public void setJournalType(String journalType) {
        this.journalType = journalType;
    }

    public DeploymentPlan withJournalType(String journalType) {
        this.journalType = journalType;
        return this;
    }

    /**
     * If true enable the management role based access control
     */
    @JsonProperty("managementRBACEnabled")
    public Boolean getManagementRBACEnabled() {
        return managementRBACEnabled;
    }

    /**
     * If true enable the management role based access control
     */
    @JsonProperty("managementRBACEnabled")
    public void setManagementRBACEnabled(Boolean managementRBACEnabled) {
        this.managementRBACEnabled = managementRBACEnabled;
    }

    public DeploymentPlan withManagementRBACEnabled(Boolean managementRBACEnabled) {
        this.managementRBACEnabled = managementRBACEnabled;
        return this;
    }

    /**
     * If true migrate messages on scaledown
     */
    @JsonProperty("messageMigration")
    public Boolean getMessageMigration() {
        return messageMigration;
    }

    /**
     * If true migrate messages on scaledown
     */
    @JsonProperty("messageMigration")
    public void setMessageMigration(Boolean messageMigration) {
        this.messageMigration = messageMigration;
    }

    public DeploymentPlan withMessageMigration(Boolean messageMigration) {
        this.messageMigration = messageMigration;
        return this;
    }

    /**
     * If true use persistent volume via persistent volume claim for journal storage
     */
    @JsonProperty("persistenceEnabled")
    public Boolean getPersistenceEnabled() {
        return persistenceEnabled;
    }

    /**
     * If true use persistent volume via persistent volume claim for journal storage
     */
    @JsonProperty("persistenceEnabled")
    public void setPersistenceEnabled(Boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
    }

    public DeploymentPlan withPersistenceEnabled(Boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
        return this;
    }

    /**
     * If true require user password login credentials for broker protocol ports
     */
    @JsonProperty("requireLogin")
    public Boolean getRequireLogin() {
        return requireLogin;
    }

    /**
     * If true require user password login credentials for broker protocol ports
     */
    @JsonProperty("requireLogin")
    public void setRequireLogin(Boolean requireLogin) {
        this.requireLogin = requireLogin;
    }

    public DeploymentPlan withRequireLogin(Boolean requireLogin) {
        this.requireLogin = requireLogin;
        return this;
    }

    /**
     * The number of broker pods to deploy
     */
    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    /**
     * The number of broker pods to deploy
     */
    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

    public DeploymentPlan withSize(Integer size) {
        this.size = size;
        return this;
    }

    /**
     * the storage capacity
     */
    @JsonProperty("storage")
    public Storage getStorage() {
        return storage;
    }

    /**
     * the storage capacity
     */
    @JsonProperty("storage")
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public DeploymentPlan withStorage(Storage storage) {
        this.storage = storage;
        return this;
    }

    @JsonProperty("resources")
    public Resources getResources() {
        return resources;
    }

    @JsonProperty("resources")
    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public DeploymentPlan withResources(Resources resources) {
        this.resources = resources;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public DeploymentPlan withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DeploymentPlan.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("jolokiaAgentEnabled");
        sb.append('=');
        sb.append(((this.jolokiaAgentEnabled == null) ? "<null>" : this.jolokiaAgentEnabled));
        sb.append(',');
        sb.append("image");
        sb.append('=');
        sb.append(((this.image == null) ? "<null>" : this.image));
        sb.append(',');
        sb.append("initImage");
        sb.append('=');
        sb.append(((this.initImage == null) ? "<null>" : this.initImage));
        sb.append(',');
        sb.append("journalType");
        sb.append('=');
        sb.append(((this.journalType == null) ? "<null>" : this.journalType));
        sb.append(',');
        sb.append("managementRBACEnabled");
        sb.append('=');
        sb.append(((this.managementRBACEnabled == null) ? "<null>" : this.managementRBACEnabled));
        sb.append(',');
        sb.append("messageMigration");
        sb.append('=');
        sb.append(((this.messageMigration == null) ? "<null>" : this.messageMigration));
        sb.append(',');
        sb.append("persistenceEnabled");
        sb.append('=');
        sb.append(((this.persistenceEnabled == null) ? "<null>" : this.persistenceEnabled));
        sb.append(',');
        sb.append("requireLogin");
        sb.append('=');
        sb.append(((this.requireLogin == null) ? "<null>" : this.requireLogin));
        sb.append(',');
        sb.append("size");
        sb.append('=');
        sb.append(((this.size == null) ? "<null>" : this.size));
        sb.append(',');
        sb.append("storage");
        sb.append('=');
        sb.append(((this.storage == null) ? "<null>" : this.storage));
        sb.append(',');
        sb.append("resources");
        sb.append('=');
        sb.append(((this.resources == null) ? "<null>" : this.resources));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null) ? "<null>" : this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((this.image == null) ? 0 : this.image.hashCode()));
        result = ((result * 31) + ((this.initImage == null) ? 0 : this.initImage.hashCode()));
        result = ((result * 31) + ((this.requireLogin == null) ? 0 : this.requireLogin.hashCode()));
        result = ((result * 31) + ((this.resources == null) ? 0 : this.resources.hashCode()));
        result = ((result * 31) + ((this.storage == null) ? 0 : this.storage.hashCode()));
        result = ((result * 31) + ((this.persistenceEnabled == null) ? 0 : this.persistenceEnabled.hashCode()));
        result = ((result * 31) + ((this.jolokiaAgentEnabled == null) ? 0 : this.jolokiaAgentEnabled.hashCode()));
        result = ((result * 31) + ((this.size == null) ? 0 : this.size.hashCode()));
        result = ((result * 31) + ((this.journalType == null) ? 0 : this.journalType.hashCode()));
        result = ((result * 31) + ((this.managementRBACEnabled == null) ? 0 : this.managementRBACEnabled.hashCode()));
        result = ((result * 31) + ((this.messageMigration == null) ? 0 : this.messageMigration.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DeploymentPlan) == false) {
            return false;
        }
        DeploymentPlan rhs = ((DeploymentPlan) other);
        return ((((((
            (((((((this.image == rhs.image) || ((this.image != null) && this.image.equals(rhs.image))) && ((this.initImage == rhs.initImage) || (
                (this.initImage != null) && this.initImage.equals(rhs.initImage)))) && ((this.requireLogin == rhs.requireLogin) || (
                (this.requireLogin != null) && this.requireLogin.equals(rhs.requireLogin)))) && ((this.resources == rhs.resources) || (
                (this.resources != null) && this.resources.equals(rhs.resources)))) && ((this.storage == rhs.storage) || ((this.storage != null)
                && this.storage.equals(rhs.storage)))) && ((this.persistenceEnabled == rhs.persistenceEnabled) || ((this.persistenceEnabled != null)
                && this.persistenceEnabled.equals(rhs.persistenceEnabled)))) && ((this.jolokiaAgentEnabled == rhs.jolokiaAgentEnabled) || (
                (this.jolokiaAgentEnabled != null) && this.jolokiaAgentEnabled.equals(rhs.jolokiaAgentEnabled)))) && ((this.size == rhs.size) || (
            (this.size != null) && this.size.equals(rhs.size)))) && ((this.journalType == rhs.journalType) || ((this.journalType != null)
            && this.journalType.equals(rhs.journalType)))) && ((this.managementRBACEnabled == rhs.managementRBACEnabled) || (
            (this.managementRBACEnabled != null) && this.managementRBACEnabled.equals(rhs.managementRBACEnabled)))) && (
            (this.messageMigration == rhs.messageMigration) || ((this.messageMigration != null) && this.messageMigration
                .equals(rhs.messageMigration)))) && ((this.additionalProperties == rhs.additionalProperties) || ((this.additionalProperties != null)
            && this.additionalProperties.equals(rhs.additionalProperties))));
    }
}
