package generated.io.argoproj.v1alpha1.applicationstatus.operationstate;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"info","initiatedBy","retry","sync"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Operation implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Info is a list of informational items for this operation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("info")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Info is a list of informational items for this operation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.Info> info;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.Info> getInfo() {
        return info;
    }

    public void setInfo(java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.Info> info) {
        this.info = info;
    }

    /**
     * InitiatedBy contains information about who initiated the operations
     */
    @com.fasterxml.jackson.annotation.JsonProperty("initiatedBy")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("InitiatedBy contains information about who initiated the operations")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.InitiatedBy initiatedBy;

    public generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.InitiatedBy getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.InitiatedBy initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    /**
     * Retry controls the strategy to apply if a sync fails
     */
    @com.fasterxml.jackson.annotation.JsonProperty("retry")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Retry controls the strategy to apply if a sync fails")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.Retry retry;

    public generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.Retry getRetry() {
        return retry;
    }

    public void setRetry(generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.Retry retry) {
        this.retry = retry;
    }

    /**
     * Sync contains parameters for the operation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sync")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Sync contains parameters for the operation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.Sync sync;

    public generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.Sync getSync() {
        return sync;
    }

    public void setSync(generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.Sync sync) {
        this.sync = sync;
    }
}

