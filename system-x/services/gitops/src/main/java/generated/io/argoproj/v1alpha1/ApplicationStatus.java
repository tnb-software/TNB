package generated.io.argoproj.v1alpha1;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"conditions","controllerNamespace","health","history","observedAt","operationState","reconciledAt","resourceHealthSource","resources","sourceType","sourceTypes","summary","sync"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class ApplicationStatus implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Conditions is a list of currently observed application conditions
     */
    @com.fasterxml.jackson.annotation.JsonProperty("conditions")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Conditions is a list of currently observed application conditions")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.Conditions> conditions;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.Conditions> getConditions() {
        return conditions;
    }

    public void setConditions(java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.Conditions> conditions) {
        this.conditions = conditions;
    }

    /**
     * ControllerNamespace indicates the namespace in which the application controller is located
     */
    @com.fasterxml.jackson.annotation.JsonProperty("controllerNamespace")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ControllerNamespace indicates the namespace in which the application controller is located")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String controllerNamespace;

    public String getControllerNamespace() {
        return controllerNamespace;
    }

    public void setControllerNamespace(String controllerNamespace) {
        this.controllerNamespace = controllerNamespace;
    }

    /**
     * Health contains information about the application's current health status
     */
    @com.fasterxml.jackson.annotation.JsonProperty("health")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Health contains information about the application's current health status")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.Health health;

    public generated.io.argoproj.v1alpha1.applicationstatus.Health getHealth() {
        return health;
    }

    public void setHealth(generated.io.argoproj.v1alpha1.applicationstatus.Health health) {
        this.health = health;
    }

    /**
     * History contains information about the application's sync history
     */
    @com.fasterxml.jackson.annotation.JsonProperty("history")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("History contains information about the application's sync history")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.History> history;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.History> getHistory() {
        return history;
    }

    public void setHistory(java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.History> history) {
        this.history = history;
    }

    /**
     * ObservedAt indicates when the application state was updated without querying latest git state Deprecated: controller no longer updates ObservedAt field
     */
    @com.fasterxml.jackson.annotation.JsonProperty("observedAt")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ObservedAt indicates when the application state was updated without querying latest git state Deprecated: controller no longer updates ObservedAt field")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String observedAt;

    public String getObservedAt() {
        return observedAt;
    }

    public void setObservedAt(String observedAt) {
        this.observedAt = observedAt;
    }

    /**
     * OperationState contains information about any ongoing operations, such as a sync
     */
    @com.fasterxml.jackson.annotation.JsonProperty("operationState")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("OperationState contains information about any ongoing operations, such as a sync")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.OperationState operationState;

    public generated.io.argoproj.v1alpha1.applicationstatus.OperationState getOperationState() {
        return operationState;
    }

    public void setOperationState(generated.io.argoproj.v1alpha1.applicationstatus.OperationState operationState) {
        this.operationState = operationState;
    }

    /**
     * ReconciledAt indicates when the application state was reconciled using the latest git version
     */
    @com.fasterxml.jackson.annotation.JsonProperty("reconciledAt")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ReconciledAt indicates when the application state was reconciled using the latest git version")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String reconciledAt;

    public String getReconciledAt() {
        return reconciledAt;
    }

    public void setReconciledAt(String reconciledAt) {
        this.reconciledAt = reconciledAt;
    }

    /**
     * ResourceHealthSource indicates where the resource health status is stored: inline if not set or appTree
     */
    @com.fasterxml.jackson.annotation.JsonProperty("resourceHealthSource")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ResourceHealthSource indicates where the resource health status is stored: inline if not set or appTree")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String resourceHealthSource;

    public String getResourceHealthSource() {
        return resourceHealthSource;
    }

    public void setResourceHealthSource(String resourceHealthSource) {
        this.resourceHealthSource = resourceHealthSource;
    }

    /**
     * Resources is a list of Kubernetes resources managed by this application
     */
    @com.fasterxml.jackson.annotation.JsonProperty("resources")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Resources is a list of Kubernetes resources managed by this application")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.Resources> resources;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.Resources> getResources() {
        return resources;
    }

    public void setResources(java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.Resources> resources) {
        this.resources = resources;
    }

    /**
     * SourceType specifies the type of this application
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sourceType")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("SourceType specifies the type of this application")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String sourceType;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * SourceTypes specifies the type of the sources included in the application
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sourceTypes")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("SourceTypes specifies the type of the sources included in the application")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> sourceTypes;

    public java.util.List<String> getSourceTypes() {
        return sourceTypes;
    }

    public void setSourceTypes(java.util.List<String> sourceTypes) {
        this.sourceTypes = sourceTypes;
    }

    /**
     * Summary contains a list of URLs and container images used by this application
     */
    @com.fasterxml.jackson.annotation.JsonProperty("summary")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Summary contains a list of URLs and container images used by this application")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.Summary summary;

    public generated.io.argoproj.v1alpha1.applicationstatus.Summary getSummary() {
        return summary;
    }

    public void setSummary(generated.io.argoproj.v1alpha1.applicationstatus.Summary summary) {
        this.summary = summary;
    }

    /**
     * Sync contains information about the application's current sync status
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sync")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Sync contains information about the application's current sync status")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.Sync sync;

    public generated.io.argoproj.v1alpha1.applicationstatus.Sync getSync() {
        return sync;
    }

    public void setSync(generated.io.argoproj.v1alpha1.applicationstatus.Sync sync) {
        this.sync = sync;
    }
}

