package generated.io.argoproj.v1alpha1.applicationspec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"automated","managedNamespaceMetadata","retry","syncOptions"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class SyncPolicy implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Automated will keep an application synced to the target revision
     */
    @com.fasterxml.jackson.annotation.JsonProperty("automated")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Automated will keep an application synced to the target revision")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationspec.syncpolicy.Automated automated;

    public generated.io.argoproj.v1alpha1.applicationspec.syncpolicy.Automated getAutomated() {
        return automated;
    }

    public void setAutomated(generated.io.argoproj.v1alpha1.applicationspec.syncpolicy.Automated automated) {
        this.automated = automated;
    }

    /**
     * ManagedNamespaceMetadata controls metadata in the given namespace (if CreateNamespace=true)
     */
    @com.fasterxml.jackson.annotation.JsonProperty("managedNamespaceMetadata")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ManagedNamespaceMetadata controls metadata in the given namespace (if CreateNamespace=true)")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationspec.syncpolicy.ManagedNamespaceMetadata managedNamespaceMetadata;

    public generated.io.argoproj.v1alpha1.applicationspec.syncpolicy.ManagedNamespaceMetadata getManagedNamespaceMetadata() {
        return managedNamespaceMetadata;
    }

    public void setManagedNamespaceMetadata(generated.io.argoproj.v1alpha1.applicationspec.syncpolicy.ManagedNamespaceMetadata managedNamespaceMetadata) {
        this.managedNamespaceMetadata = managedNamespaceMetadata;
    }

    /**
     * Retry controls failed sync retry behavior
     */
    @com.fasterxml.jackson.annotation.JsonProperty("retry")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Retry controls failed sync retry behavior")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationspec.syncpolicy.Retry retry;

    public generated.io.argoproj.v1alpha1.applicationspec.syncpolicy.Retry getRetry() {
        return retry;
    }

    public void setRetry(generated.io.argoproj.v1alpha1.applicationspec.syncpolicy.Retry retry) {
        this.retry = retry;
    }

    /**
     * Options allow you to specify whole app sync-options
     */
    @com.fasterxml.jackson.annotation.JsonProperty("syncOptions")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Options allow you to specify whole app sync-options")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> syncOptions;

    public java.util.List<String> getSyncOptions() {
        return syncOptions;
    }

    public void setSyncOptions(java.util.List<String> syncOptions) {
        this.syncOptions = syncOptions;
    }
}

