package generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"dryRun","manifests","prune","resources","revision","revisions","source","sources","syncOptions","syncStrategy"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Sync implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * DryRun specifies to perform a `kubectl apply --dry-run` without actually performing the sync
     */
    @com.fasterxml.jackson.annotation.JsonProperty("dryRun")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("DryRun specifies to perform a `kubectl apply --dry-run` without actually performing the sync")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean dryRun;

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * Manifests is an optional field that overrides sync source with a local directory for development
     */
    @com.fasterxml.jackson.annotation.JsonProperty("manifests")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Manifests is an optional field that overrides sync source with a local directory for development")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> manifests;

    public java.util.List<String> getManifests() {
        return manifests;
    }

    public void setManifests(java.util.List<String> manifests) {
        this.manifests = manifests;
    }

    /**
     * Prune specifies to delete resources from the cluster that are no longer tracked in git
     */
    @com.fasterxml.jackson.annotation.JsonProperty("prune")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Prune specifies to delete resources from the cluster that are no longer tracked in git")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean prune;

    public Boolean getPrune() {
        return prune;
    }

    public void setPrune(Boolean prune) {
        this.prune = prune;
    }

    /**
     * Resources describes which resources shall be part of the sync
     */
    @com.fasterxml.jackson.annotation.JsonProperty("resources")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Resources describes which resources shall be part of the sync")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.Resources> resources;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.Resources> getResources() {
        return resources;
    }

    public void setResources(java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.Resources> resources) {
        this.resources = resources;
    }

    /**
     * Revision is the revision (Git) or chart version (Helm) which to sync the application to If omitted, will use the revision specified in app spec.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("revision")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Revision is the revision (Git) or chart version (Helm) which to sync the application to If omitted, will use the revision specified in app spec.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String revision;

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * Revisions is the list of revision (Git) or chart version (Helm) which to sync each source in sources field for the application to If omitted, will use the revision specified in app spec.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("revisions")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Revisions is the list of revision (Git) or chart version (Helm) which to sync each source in sources field for the application to If omitted, will use the revision specified in app spec.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> revisions;

    public java.util.List<String> getRevisions() {
        return revisions;
    }

    public void setRevisions(java.util.List<String> revisions) {
        this.revisions = revisions;
    }

    /**
     * Source overrides the source definition set in the application. This is typically set in a Rollback operation and is nil during a Sync operation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("source")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Source overrides the source definition set in the application. This is typically set in a Rollback operation and is nil during a Sync operation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.Source source;

    public generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.Source getSource() {
        return source;
    }

    public void setSource(generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.Source source) {
        this.source = source;
    }

    /**
     * Sources overrides the source definition set in the application. This is typically set in a Rollback operation and is nil during a Sync operation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sources")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Sources overrides the source definition set in the application. This is typically set in a Rollback operation and is nil during a Sync operation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.Sources> sources;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.Sources> getSources() {
        return sources;
    }

    public void setSources(java.util.List<generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.Sources> sources) {
        this.sources = sources;
    }

    /**
     * SyncOptions provide per-sync sync-options, e.g. Validate=false
     */
    @com.fasterxml.jackson.annotation.JsonProperty("syncOptions")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("SyncOptions provide per-sync sync-options, e.g. Validate=false")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> syncOptions;

    public java.util.List<String> getSyncOptions() {
        return syncOptions;
    }

    public void setSyncOptions(java.util.List<String> syncOptions) {
        this.syncOptions = syncOptions;
    }

    /**
     * SyncStrategy describes how to perform the sync
     */
    @com.fasterxml.jackson.annotation.JsonProperty("syncStrategy")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("SyncStrategy describes how to perform the sync")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.SyncStrategy syncStrategy;

    public generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.SyncStrategy getSyncStrategy() {
        return syncStrategy;
    }

    public void setSyncStrategy(generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation.sync.SyncStrategy syncStrategy) {
        this.syncStrategy = syncStrategy;
    }
}

