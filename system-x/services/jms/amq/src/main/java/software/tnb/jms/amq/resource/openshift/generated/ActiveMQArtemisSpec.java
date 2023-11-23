package software.tnb.jms.amq.resource.openshift.generated;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"acceptors","addressSettings","adminPassword","adminUser","brokerProperties","connectors","console","deploymentPlan","env","ingressDomain","upgrades","version"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class ActiveMQArtemisSpec implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Specifies the acceptor configuration
     */
    @com.fasterxml.jackson.annotation.JsonProperty("acceptors")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the acceptor configuration")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Acceptors> acceptors;

    public java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Acceptors> getAcceptors() {
        return acceptors;
    }

    public void setAcceptors(java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Acceptors> acceptors) {
        this.acceptors = acceptors;
    }

    /**
     * Specifies the address configurations
     */
    @com.fasterxml.jackson.annotation.JsonProperty("addressSettings")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the address configurations")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.AddressSettings addressSettings;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.AddressSettings getAddressSettings() {
        return addressSettings;
    }

    public void setAddressSettings(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.AddressSettings addressSettings) {
        this.addressSettings = addressSettings;
    }

    /**
     * Password for standard broker user. It is required for connecting to the broker and the web console. If left empty, it will be generated.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("adminPassword")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Password for standard broker user. It is required for connecting to the broker and the web console. If left empty, it will be generated.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String adminPassword;

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    /**
     * User name for standard broker user. It is required for connecting to the broker and the web console. If left empty, it will be generated.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("adminUser")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("User name for standard broker user. It is required for connecting to the broker and the web console. If left empty, it will be generated.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String adminUser;

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    /**
     * Optional list of key=value properties that are applied to the broker configuration bean.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("brokerProperties")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Optional list of key=value properties that are applied to the broker configuration bean.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> brokerProperties;

    public java.util.List<String> getBrokerProperties() {
        return brokerProperties;
    }

    public void setBrokerProperties(java.util.List<String> brokerProperties) {
        this.brokerProperties = brokerProperties;
    }

    /**
     * Specifies connectors and connector configuration
     */
    @com.fasterxml.jackson.annotation.JsonProperty("connectors")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies connectors and connector configuration")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Connectors> connectors;

    public java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Connectors> getConnectors() {
        return connectors;
    }

    public void setConnectors(java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Connectors> connectors) {
        this.connectors = connectors;
    }

    /**
     * Specifies the console configuration
     */
    @com.fasterxml.jackson.annotation.JsonProperty("console")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the console configuration")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Console console;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Console getConsole() {
        return console;
    }

    public void setConsole(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Console console) {
        this.console = console;
    }

    /**
     * Specifies the deployment plan
     */
    @com.fasterxml.jackson.annotation.JsonProperty("deploymentPlan")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the deployment plan")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.DeploymentPlan deploymentPlan;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.DeploymentPlan getDeploymentPlan() {
        return deploymentPlan;
    }

    public void setDeploymentPlan(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.DeploymentPlan deploymentPlan) {
        this.deploymentPlan = deploymentPlan;
    }

    /**
     * Optional list of environment variables to apply to the container(s), not exclusive
     */
    @com.fasterxml.jackson.annotation.JsonProperty("env")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Optional list of environment variables to apply to the container(s), not exclusive")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Env> env;

    public java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Env> getEnv() {
        return env;
    }

    public void setEnv(java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Env> env) {
        this.env = env;
    }

    /**
     * The ingress domain to expose the application. By default, on Kubernetes it is apps.artemiscloud.io and on OpenShift it is the Ingress Controller domain.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ingressDomain")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The ingress domain to expose the application. By default, on Kubernetes it is apps.artemiscloud.io and on OpenShift it is the Ingress Controller domain.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String ingressDomain;

    public String getIngressDomain() {
        return ingressDomain;
    }

    public void setIngressDomain(String ingressDomain) {
        this.ingressDomain = ingressDomain;
    }

    /**
     * Specifies the upgrades (deprecated in favour of Version)
     */
    @com.fasterxml.jackson.annotation.JsonProperty("upgrades")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the upgrades (deprecated in favour of Version)")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Upgrades upgrades;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Upgrades getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Upgrades upgrades) {
        this.upgrades = upgrades;
    }

    /**
     * The desired version of the broker. Can be x, or x.y or x.y.z to configure upgrades
     */
    @com.fasterxml.jackson.annotation.JsonProperty("version")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The desired version of the broker. Can be x, or x.y or x.y.z to configure upgrades")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

