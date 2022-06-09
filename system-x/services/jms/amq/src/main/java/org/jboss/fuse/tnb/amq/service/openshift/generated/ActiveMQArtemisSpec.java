package org.jboss.fuse.tnb.amq.service.openshift.generated;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.KubernetesResource;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "acceptors",
    "adminPassword",
    "adminUser",
    "connectors",
    "addressSettings",
    "console",
    "deploymentPlan",
    "upgrades",
    "version"
})
@Generated("jsonschema2pojo")
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class ActiveMQArtemisSpec implements KubernetesResource {

    /**
     * Configuration of all acceptors
     */
    @JsonProperty("acceptors")
    @JsonPropertyDescription("Configuration of all acceptors")
    private List<Acceptor> acceptors = new ArrayList<Acceptor>();
    /**
     * Password for standard broker user. It is required for connecting to the broker. If left empty, it will be generated.
     */
    @JsonProperty("adminPassword")
    @JsonPropertyDescription("Password for standard broker user. It is required for connecting to the broker. If left empty, it will be generated.")
    private String adminPassword;
    /**
     * User name for standard broker user. It is required for connecting to the broker. If left empty, it will be generated.
     */
    @JsonProperty("adminUser")
    @JsonPropertyDescription("User name for standard broker user. It is required for connecting to the broker. If left empty, it will be generated.")
    private String adminUser;
    /**
     * Configuration of all connectors
     */
    @JsonProperty("connectors")
    @JsonPropertyDescription("Configuration of all connectors")
    private List<Connector> connectors = new ArrayList<Connector>();
    /**
     * a list of address settings
     */
    @JsonProperty("addressSettings")
    @JsonPropertyDescription("a list of address settings")
    private AddressSettings addressSettings;
    /**
     * Configuration for the embedded web console
     */
    @JsonProperty("console")
    @JsonPropertyDescription("Configuration for the embedded web console")
    private Console console;
    @JsonProperty("deploymentPlan")
    private DeploymentPlan deploymentPlan;
    /**
     * Specify the level of upgrade that should be allowed when an older product version is detected
     */
    @JsonProperty("upgrades")
    @JsonPropertyDescription("Specify the level of upgrade that should be allowed when an older product version is detected")
    private Upgrades upgrades;
    /**
     * The version of the application deployment.
     */
    @JsonProperty("version")
    @JsonPropertyDescription("The version of the application deployment.")
    private String version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Configuration of all acceptors
     */
    @JsonProperty("acceptors")
    public List<Acceptor> getAcceptors() {
        return acceptors;
    }

    /**
     * Configuration of all acceptors
     */
    @JsonProperty("acceptors")
    public void setAcceptors(List<Acceptor> acceptors) {
        this.acceptors = acceptors;
    }

    public ActiveMQArtemisSpec withAcceptors(List<Acceptor> acceptors) {
        this.acceptors = acceptors;
        return this;
    }

    /**
     * Password for standard broker user. It is required for connecting to the broker. If left empty, it will be generated.
     */
    @JsonProperty("adminPassword")
    public String getAdminPassword() {
        return adminPassword;
    }

    /**
     * Password for standard broker user. It is required for connecting to the broker. If left empty, it will be generated.
     */
    @JsonProperty("adminPassword")
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public ActiveMQArtemisSpec withAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
        return this;
    }

    /**
     * User name for standard broker user. It is required for connecting to the broker. If left empty, it will be generated.
     */
    @JsonProperty("adminUser")
    public String getAdminUser() {
        return adminUser;
    }

    /**
     * User name for standard broker user. It is required for connecting to the broker. If left empty, it will be generated.
     */
    @JsonProperty("adminUser")
    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public ActiveMQArtemisSpec withAdminUser(String adminUser) {
        this.adminUser = adminUser;
        return this;
    }

    /**
     * Configuration of all connectors
     */
    @JsonProperty("connectors")
    public List<Connector> getConnectors() {
        return connectors;
    }

    /**
     * Configuration of all connectors
     */
    @JsonProperty("connectors")
    public void setConnectors(List<Connector> connectors) {
        this.connectors = connectors;
    }

    public ActiveMQArtemisSpec withConnectors(List<Connector> connectors) {
        this.connectors = connectors;
        return this;
    }

    /**
     * a list of address settings
     */
    @JsonProperty("addressSettings")
    public AddressSettings getAddressSettings() {
        return addressSettings;
    }

    /**
     * a list of address settings
     */
    @JsonProperty("addressSettings")
    public void setAddressSettings(AddressSettings addressSettings) {
        this.addressSettings = addressSettings;
    }

    public ActiveMQArtemisSpec withAddressSettings(AddressSettings addressSettings) {
        this.addressSettings = addressSettings;
        return this;
    }

    /**
     * Configuration for the embedded web console
     */
    @JsonProperty("console")
    public Console getConsole() {
        return console;
    }

    /**
     * Configuration for the embedded web console
     */
    @JsonProperty("console")
    public void setConsole(Console console) {
        this.console = console;
    }

    public ActiveMQArtemisSpec withConsole(Console console) {
        this.console = console;
        return this;
    }

    @JsonProperty("deploymentPlan")
    public DeploymentPlan getDeploymentPlan() {
        return deploymentPlan;
    }

    @JsonProperty("deploymentPlan")
    public void setDeploymentPlan(DeploymentPlan deploymentPlan) {
        this.deploymentPlan = deploymentPlan;
    }

    public ActiveMQArtemisSpec withDeploymentPlan(DeploymentPlan deploymentPlan) {
        this.deploymentPlan = deploymentPlan;
        return this;
    }

    /**
     * Specify the level of upgrade that should be allowed when an older product version is detected
     */
    @JsonProperty("upgrades")
    public Upgrades getUpgrades() {
        return upgrades;
    }

    /**
     * Specify the level of upgrade that should be allowed when an older product version is detected
     */
    @JsonProperty("upgrades")
    public void setUpgrades(Upgrades upgrades) {
        this.upgrades = upgrades;
    }

    public ActiveMQArtemisSpec withUpgrades(Upgrades upgrades) {
        this.upgrades = upgrades;
        return this;
    }

    /**
     * The version of the application deployment.
     */
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    /**
     * The version of the application deployment.
     */
    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    public ActiveMQArtemisSpec withVersion(String version) {
        this.version = version;
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

    public ActiveMQArtemisSpec withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ActiveMQArtemisSpec.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("acceptors");
        sb.append('=');
        sb.append(((this.acceptors == null) ? "<null>" : this.acceptors));
        sb.append(',');
        sb.append("adminPassword");
        sb.append('=');
        sb.append(((this.adminPassword == null) ? "<null>" : this.adminPassword));
        sb.append(',');
        sb.append("adminUser");
        sb.append('=');
        sb.append(((this.adminUser == null) ? "<null>" : this.adminUser));
        sb.append(',');
        sb.append("connectors");
        sb.append('=');
        sb.append(((this.connectors == null) ? "<null>" : this.connectors));
        sb.append(',');
        sb.append("addressSettings");
        sb.append('=');
        sb.append(((this.addressSettings == null) ? "<null>" : this.addressSettings));
        sb.append(',');
        sb.append("console");
        sb.append('=');
        sb.append(((this.console == null) ? "<null>" : this.console));
        sb.append(',');
        sb.append("deploymentPlan");
        sb.append('=');
        sb.append(((this.deploymentPlan == null) ? "<null>" : this.deploymentPlan));
        sb.append(',');
        sb.append("upgrades");
        sb.append('=');
        sb.append(((this.upgrades == null) ? "<null>" : this.upgrades));
        sb.append(',');
        sb.append("version");
        sb.append('=');
        sb.append(((this.version == null) ? "<null>" : this.version));
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
        result = ((result * 31) + ((this.console == null) ? 0 : this.console.hashCode()));
        result = ((result * 31) + ((this.connectors == null) ? 0 : this.connectors.hashCode()));
        result = ((result * 31) + ((this.adminUser == null) ? 0 : this.adminUser.hashCode()));
        result = ((result * 31) + ((this.addressSettings == null) ? 0 : this.addressSettings.hashCode()));
        result = ((result * 31) + ((this.deploymentPlan == null) ? 0 : this.deploymentPlan.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        result = ((result * 31) + ((this.acceptors == null) ? 0 : this.acceptors.hashCode()));
        result = ((result * 31) + ((this.upgrades == null) ? 0 : this.upgrades.hashCode()));
        result = ((result * 31) + ((this.version == null) ? 0 : this.version.hashCode()));
        result = ((result * 31) + ((this.adminPassword == null) ? 0 : this.adminPassword.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ActiveMQArtemisSpec) == false) {
            return false;
        }
        ActiveMQArtemisSpec rhs = ((ActiveMQArtemisSpec) other);
        return ((((((
            (((((this.console == rhs.console) || ((this.console != null) && this.console.equals(rhs.console))) && ((this.connectors == rhs.connectors)
                || ((this.connectors != null) && this.connectors.equals(rhs.connectors)))) && ((this.adminUser == rhs.adminUser) || (
                (this.adminUser != null) && this.adminUser.equals(rhs.adminUser)))) && ((this.addressSettings == rhs.addressSettings) || (
                (this.addressSettings != null) && this.addressSettings.equals(rhs.addressSettings)))) && ((this.deploymentPlan == rhs.deploymentPlan)
                || ((this.deploymentPlan != null) && this.deploymentPlan.equals(rhs.deploymentPlan)))) && (
            (this.additionalProperties == rhs.additionalProperties) || ((this.additionalProperties != null) && this.additionalProperties
                .equals(rhs.additionalProperties)))) && ((this.acceptors == rhs.acceptors) || ((this.acceptors != null) && this.acceptors
            .equals(rhs.acceptors)))) && ((this.upgrades == rhs.upgrades) || ((this.upgrades != null) && this.upgrades.equals(rhs.upgrades)))) && (
            (this.version == rhs.version) || ((this.version != null) && this.version.equals(rhs.version)))) && (
            (this.adminPassword == rhs.adminPassword) || ((this.adminPassword != null) && this.adminPassword.equals(rhs.adminPassword))));
    }
}
