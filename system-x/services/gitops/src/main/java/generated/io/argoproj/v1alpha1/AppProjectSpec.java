package generated.io.argoproj.v1alpha1;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"clusterResourceBlacklist","clusterResourceWhitelist","destinations","namespaceResourceBlacklist","namespaceResourceWhitelist","orphanedResources","permitOnlyProjectScopedClusters","roles","signatureKeys","sourceNamespaces","sourceRepos","syncWindows"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class AppProjectSpec implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * ClusterResourceBlacklist contains list of blacklisted cluster level resources
     */
    @com.fasterxml.jackson.annotation.JsonProperty("clusterResourceBlacklist")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ClusterResourceBlacklist contains list of blacklisted cluster level resources")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.ClusterResourceBlacklist> clusterResourceBlacklist;

    public java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.ClusterResourceBlacklist> getClusterResourceBlacklist() {
        return clusterResourceBlacklist;
    }

    public void setClusterResourceBlacklist(java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.ClusterResourceBlacklist> clusterResourceBlacklist) {
        this.clusterResourceBlacklist = clusterResourceBlacklist;
    }

    /**
     * ClusterResourceWhitelist contains list of whitelisted cluster level resources
     */
    @com.fasterxml.jackson.annotation.JsonProperty("clusterResourceWhitelist")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ClusterResourceWhitelist contains list of whitelisted cluster level resources")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.ClusterResourceWhitelist> clusterResourceWhitelist;

    public java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.ClusterResourceWhitelist> getClusterResourceWhitelist() {
        return clusterResourceWhitelist;
    }

    public void setClusterResourceWhitelist(java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.ClusterResourceWhitelist> clusterResourceWhitelist) {
        this.clusterResourceWhitelist = clusterResourceWhitelist;
    }

    /**
     * Destinations contains list of destinations available for deployment
     */
    @com.fasterxml.jackson.annotation.JsonProperty("destinations")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Destinations contains list of destinations available for deployment")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.Destinations> destinations;

    public java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.Destinations> getDestinations() {
        return destinations;
    }

    public void setDestinations(java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.Destinations> destinations) {
        this.destinations = destinations;
    }

    /**
     * NamespaceResourceBlacklist contains list of blacklisted namespace level resources
     */
    @com.fasterxml.jackson.annotation.JsonProperty("namespaceResourceBlacklist")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("NamespaceResourceBlacklist contains list of blacklisted namespace level resources")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.NamespaceResourceBlacklist> namespaceResourceBlacklist;

    public java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.NamespaceResourceBlacklist> getNamespaceResourceBlacklist() {
        return namespaceResourceBlacklist;
    }

    public void setNamespaceResourceBlacklist(java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.NamespaceResourceBlacklist> namespaceResourceBlacklist) {
        this.namespaceResourceBlacklist = namespaceResourceBlacklist;
    }

    /**
     * NamespaceResourceWhitelist contains list of whitelisted namespace level resources
     */
    @com.fasterxml.jackson.annotation.JsonProperty("namespaceResourceWhitelist")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("NamespaceResourceWhitelist contains list of whitelisted namespace level resources")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.NamespaceResourceWhitelist> namespaceResourceWhitelist;

    public java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.NamespaceResourceWhitelist> getNamespaceResourceWhitelist() {
        return namespaceResourceWhitelist;
    }

    public void setNamespaceResourceWhitelist(java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.NamespaceResourceWhitelist> namespaceResourceWhitelist) {
        this.namespaceResourceWhitelist = namespaceResourceWhitelist;
    }

    /**
     * OrphanedResources specifies if controller should monitor orphaned resources of apps in this project
     */
    @com.fasterxml.jackson.annotation.JsonProperty("orphanedResources")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("OrphanedResources specifies if controller should monitor orphaned resources of apps in this project")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.appprojectspec.OrphanedResources orphanedResources;

    public generated.io.argoproj.v1alpha1.appprojectspec.OrphanedResources getOrphanedResources() {
        return orphanedResources;
    }

    public void setOrphanedResources(generated.io.argoproj.v1alpha1.appprojectspec.OrphanedResources orphanedResources) {
        this.orphanedResources = orphanedResources;
    }

    /**
     * PermitOnlyProjectScopedClusters determines whether destinations can only reference clusters which are project-scoped
     */
    @com.fasterxml.jackson.annotation.JsonProperty("permitOnlyProjectScopedClusters")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("PermitOnlyProjectScopedClusters determines whether destinations can only reference clusters which are project-scoped")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean permitOnlyProjectScopedClusters;

    public Boolean getPermitOnlyProjectScopedClusters() {
        return permitOnlyProjectScopedClusters;
    }

    public void setPermitOnlyProjectScopedClusters(Boolean permitOnlyProjectScopedClusters) {
        this.permitOnlyProjectScopedClusters = permitOnlyProjectScopedClusters;
    }

    /**
     * Roles are user defined RBAC roles associated with this project
     */
    @com.fasterxml.jackson.annotation.JsonProperty("roles")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Roles are user defined RBAC roles associated with this project")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.Roles> roles;

    public java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.Roles> getRoles() {
        return roles;
    }

    public void setRoles(java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.Roles> roles) {
        this.roles = roles;
    }

    /**
     * SignatureKeys contains a list of PGP key IDs that commits in Git must be signed with in order to be allowed for sync
     */
    @com.fasterxml.jackson.annotation.JsonProperty("signatureKeys")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("SignatureKeys contains a list of PGP key IDs that commits in Git must be signed with in order to be allowed for sync")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.SignatureKeys> signatureKeys;

    public java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.SignatureKeys> getSignatureKeys() {
        return signatureKeys;
    }

    public void setSignatureKeys(java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.SignatureKeys> signatureKeys) {
        this.signatureKeys = signatureKeys;
    }

    /**
     * SourceNamespaces defines the namespaces application resources are allowed to be created in
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sourceNamespaces")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("SourceNamespaces defines the namespaces application resources are allowed to be created in")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> sourceNamespaces;

    public java.util.List<String> getSourceNamespaces() {
        return sourceNamespaces;
    }

    public void setSourceNamespaces(java.util.List<String> sourceNamespaces) {
        this.sourceNamespaces = sourceNamespaces;
    }

    /**
     * SourceRepos contains list of repository URLs which can be used for deployment
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sourceRepos")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("SourceRepos contains list of repository URLs which can be used for deployment")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> sourceRepos;

    public java.util.List<String> getSourceRepos() {
        return sourceRepos;
    }

    public void setSourceRepos(java.util.List<String> sourceRepos) {
        this.sourceRepos = sourceRepos;
    }

    /**
     * SyncWindows controls when syncs can be run for apps in this project
     */
    @com.fasterxml.jackson.annotation.JsonProperty("syncWindows")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("SyncWindows controls when syncs can be run for apps in this project")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.SyncWindows> syncWindows;

    public java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.SyncWindows> getSyncWindows() {
        return syncWindows;
    }

    public void setSyncWindows(java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.SyncWindows> syncWindows) {
        this.syncWindows = syncWindows;
    }
}

