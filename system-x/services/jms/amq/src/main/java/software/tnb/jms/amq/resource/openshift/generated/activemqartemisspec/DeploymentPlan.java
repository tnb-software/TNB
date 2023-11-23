package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"affinity","annotations","clustered","enableMetricsPlugin","extraMounts","image","initImage","jolokiaAgentEnabled","journalType","labels","livenessProbe","managementRBACEnabled","messageMigration","nodeSelector","persistenceEnabled","podDisruptionBudget","podSecurity","podSecurityContext","readinessProbe","requireLogin","resources","size","storage","tolerations"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class DeploymentPlan implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Specifies affinity configuration
     */
    @com.fasterxml.jackson.annotation.JsonProperty("affinity")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies affinity configuration")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Affinity affinity;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Affinity getAffinity() {
        return affinity;
    }

    public void setAffinity(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Affinity affinity) {
        this.affinity = affinity;
    }

    /**
     * Custom annotations to be added to broker pod
     */
    @com.fasterxml.jackson.annotation.JsonProperty("annotations")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Custom annotations to be added to broker pod")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.Map<java.lang.String, String> annotations;

    public java.util.Map<java.lang.String, String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(java.util.Map<java.lang.String, String> annotations) {
        this.annotations = annotations;
    }

    /**
     * Whether broker is clustered
     */
    @com.fasterxml.jackson.annotation.JsonProperty("clustered")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Whether broker is clustered")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean clustered;

    public Boolean getClustered() {
        return clustered;
    }

    public void setClustered(Boolean clustered) {
        this.clustered = clustered;
    }

    /**
     * Whether or not to install the artemis metrics plugin
     */
    @com.fasterxml.jackson.annotation.JsonProperty("enableMetricsPlugin")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Whether or not to install the artemis metrics plugin")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean enableMetricsPlugin;

    public Boolean getEnableMetricsPlugin() {
        return enableMetricsPlugin;
    }

    public void setEnableMetricsPlugin(Boolean enableMetricsPlugin) {
        this.enableMetricsPlugin = enableMetricsPlugin;
    }

    /**
     * Specifies extra mounts
     */
    @com.fasterxml.jackson.annotation.JsonProperty("extraMounts")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies extra mounts")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.ExtraMounts extraMounts;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.ExtraMounts getExtraMounts() {
        return extraMounts;
    }

    public void setExtraMounts(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.ExtraMounts extraMounts) {
        this.extraMounts = extraMounts;
    }

    /**
     * The image used for the broker, all upgrades are disabled. Needs a corresponding initImage
     */
    @com.fasterxml.jackson.annotation.JsonProperty("image")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The image used for the broker, all upgrades are disabled. Needs a corresponding initImage")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * The init container image used to configure broker, all upgrades are disabled. Needs a corresponding image
     */
    @com.fasterxml.jackson.annotation.JsonProperty("initImage")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The init container image used to configure broker, all upgrades are disabled. Needs a corresponding image")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String initImage;

    public String getInitImage() {
        return initImage;
    }

    public void setInitImage(String initImage) {
        this.initImage = initImage;
    }

    /**
     * If true enable the Jolokia JVM Agent
     */
    @com.fasterxml.jackson.annotation.JsonProperty("jolokiaAgentEnabled")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("If true enable the Jolokia JVM Agent")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean jolokiaAgentEnabled;

    public Boolean getJolokiaAgentEnabled() {
        return jolokiaAgentEnabled;
    }

    public void setJolokiaAgentEnabled(Boolean jolokiaAgentEnabled) {
        this.jolokiaAgentEnabled = jolokiaAgentEnabled;
    }

    /**
     * If aio use ASYNCIO, if nio use NIO for journal IO
     */
    @com.fasterxml.jackson.annotation.JsonProperty("journalType")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("If aio use ASYNCIO, if nio use NIO for journal IO")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String journalType;

    public String getJournalType() {
        return journalType;
    }

    public void setJournalType(String journalType) {
        this.journalType = journalType;
    }

    /**
     * Assign labels to a broker pod, the keys `ActiveMQArtemis` and `application` are not allowed
     */
    @com.fasterxml.jackson.annotation.JsonProperty("labels")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Assign labels to a broker pod, the keys `ActiveMQArtemis` and `application` are not allowed")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.Map<java.lang.String, String> labels;

    public java.util.Map<java.lang.String, String> getLabels() {
        return labels;
    }

    public void setLabels(java.util.Map<java.lang.String, String> labels) {
        this.labels = labels;
    }

    /**
     * Specifies the liveness probe configuration
     */
    @com.fasterxml.jackson.annotation.JsonProperty("livenessProbe")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the liveness probe configuration")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.LivenessProbe livenessProbe;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.LivenessProbe getLivenessProbe() {
        return livenessProbe;
    }

    public void setLivenessProbe(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.LivenessProbe livenessProbe) {
        this.livenessProbe = livenessProbe;
    }

    /**
     * If true enable the management role based access control
     */
    @com.fasterxml.jackson.annotation.JsonProperty("managementRBACEnabled")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("If true enable the management role based access control")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean managementRBACEnabled;

    public Boolean getManagementRBACEnabled() {
        return managementRBACEnabled;
    }

    public void setManagementRBACEnabled(Boolean managementRBACEnabled) {
        this.managementRBACEnabled = managementRBACEnabled;
    }

    /**
     * If true migrate messages on scaledown
     */
    @com.fasterxml.jackson.annotation.JsonProperty("messageMigration")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("If true migrate messages on scaledown")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean messageMigration;

    public Boolean getMessageMigration() {
        return messageMigration;
    }

    public void setMessageMigration(Boolean messageMigration) {
        this.messageMigration = messageMigration;
    }

    /**
     * Specifies the node selector
     */
    @com.fasterxml.jackson.annotation.JsonProperty("nodeSelector")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the node selector")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.Map<java.lang.String, String> nodeSelector;

    public java.util.Map<java.lang.String, String> getNodeSelector() {
        return nodeSelector;
    }

    public void setNodeSelector(java.util.Map<java.lang.String, String> nodeSelector) {
        this.nodeSelector = nodeSelector;
    }

    /**
     * If true use persistent volume via persistent volume claim for journal storage
     */
    @com.fasterxml.jackson.annotation.JsonProperty("persistenceEnabled")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("If true use persistent volume via persistent volume claim for journal storage")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean persistenceEnabled;

    public Boolean getPersistenceEnabled() {
        return persistenceEnabled;
    }

    public void setPersistenceEnabled(Boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
    }

    /**
     * Specifies the pod disruption budget
     */
    @com.fasterxml.jackson.annotation.JsonProperty("podDisruptionBudget")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the pod disruption budget")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.PodDisruptionBudget podDisruptionBudget;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.PodDisruptionBudget getPodDisruptionBudget() {
        return podDisruptionBudget;
    }

    public void setPodDisruptionBudget(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.PodDisruptionBudget podDisruptionBudget) {
        this.podDisruptionBudget = podDisruptionBudget;
    }

    /**
     * Specifies the pod security configurations
     */
    @com.fasterxml.jackson.annotation.JsonProperty("podSecurity")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the pod security configurations")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.PodSecurity podSecurity;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.PodSecurity getPodSecurity() {
        return podSecurity;
    }

    public void setPodSecurity(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.PodSecurity podSecurity) {
        this.podSecurity = podSecurity;
    }

    /**
     * Specifies the pod security context
     */
    @com.fasterxml.jackson.annotation.JsonProperty("podSecurityContext")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the pod security context")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.PodSecurityContext podSecurityContext;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.PodSecurityContext getPodSecurityContext() {
        return podSecurityContext;
    }

    public void setPodSecurityContext(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.PodSecurityContext podSecurityContext) {
        this.podSecurityContext = podSecurityContext;
    }

    /**
     * Specifies the readiness probe configuration
     */
    @com.fasterxml.jackson.annotation.JsonProperty("readinessProbe")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the readiness probe configuration")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.ReadinessProbe readinessProbe;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.ReadinessProbe getReadinessProbe() {
        return readinessProbe;
    }

    public void setReadinessProbe(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.ReadinessProbe readinessProbe) {
        this.readinessProbe = readinessProbe;
    }

    /**
     * If true require user password login credentials for broker protocol ports
     */
    @com.fasterxml.jackson.annotation.JsonProperty("requireLogin")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("If true require user password login credentials for broker protocol ports")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean requireLogin;

    public Boolean getRequireLogin() {
        return requireLogin;
    }

    public void setRequireLogin(Boolean requireLogin) {
        this.requireLogin = requireLogin;
    }

    /**
     * Specifies the minimum/maximum amount of compute resources required/allowed
     */
    @com.fasterxml.jackson.annotation.JsonProperty("resources")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the minimum/maximum amount of compute resources required/allowed")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Resources resources;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Resources getResources() {
        return resources;
    }

    public void setResources(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Resources resources) {
        this.resources = resources;
    }

    /**
     * The number of broker pods to deploy
     */
    @com.fasterxml.jackson.annotation.JsonProperty("size")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The number of broker pods to deploy")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer size;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * Specifies the storage configurations
     */
    @com.fasterxml.jackson.annotation.JsonProperty("storage")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the storage configurations")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Storage storage;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Storage getStorage() {
        return storage;
    }

    public void setStorage(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Storage storage) {
        this.storage = storage;
    }

    /**
     * Specifies the tolerations
     */
    @com.fasterxml.jackson.annotation.JsonProperty("tolerations")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the tolerations")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Tolerations> tolerations;

    public java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Tolerations> getTolerations() {
        return tolerations;
    }

    public void setTolerations(java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.Tolerations> tolerations) {
        this.tolerations = tolerations;
    }
}

