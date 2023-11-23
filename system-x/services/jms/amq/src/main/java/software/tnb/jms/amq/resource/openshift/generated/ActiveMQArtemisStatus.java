package software.tnb.jms.amq.resource.openshift.generated;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"conditions","deploymentPlanSize","externalConfigs","podStatus","scaleLabelSelector","upgrade","version"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class ActiveMQArtemisStatus implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Current state of the resource Conditions represent the latest available observations of an object's state
     */
    @com.fasterxml.jackson.annotation.JsonProperty("conditions")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Current state of the resource Conditions represent the latest available observations of an object's state")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.Conditions> conditions;

    public java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.Conditions> getConditions() {
        return conditions;
    }

    public void setConditions(java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.Conditions> conditions) {
        this.conditions = conditions;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("deploymentPlanSize")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer deploymentPlanSize;

    public Integer getDeploymentPlanSize() {
        return deploymentPlanSize;
    }

    public void setDeploymentPlanSize(Integer deploymentPlanSize) {
        this.deploymentPlanSize = deploymentPlanSize;
    }

    /**
     * Current state of external referenced resources
     */
    @com.fasterxml.jackson.annotation.JsonProperty("externalConfigs")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Current state of external referenced resources")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.ExternalConfigs> externalConfigs;

    public java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.ExternalConfigs> getExternalConfigs() {
        return externalConfigs;
    }

    public void setExternalConfigs(java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.ExternalConfigs> externalConfigs) {
        this.externalConfigs = externalConfigs;
    }

    /**
     * The current pods
     */
    @com.fasterxml.jackson.annotation.JsonProperty("podStatus")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The current pods")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.PodStatus podStatus;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.PodStatus getPodStatus() {
        return podStatus;
    }

    public void setPodStatus(software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.PodStatus podStatus) {
        this.podStatus = podStatus;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("scaleLabelSelector")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String scaleLabelSelector;

    public String getScaleLabelSelector() {
        return scaleLabelSelector;
    }

    public void setScaleLabelSelector(String scaleLabelSelector) {
        this.scaleLabelSelector = scaleLabelSelector;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("upgrade")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.Upgrade upgrade;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.Upgrade getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.Upgrade upgrade) {
        this.upgrade = upgrade;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("version")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.Version version;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.Version getVersion() {
        return version;
    }

    public void setVersion(software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus.Version version) {
        this.version = version;
    }
}

