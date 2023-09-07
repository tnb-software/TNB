package generated.io.argoproj.v1alpha1.applicationsetspec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"applicationsSync","preserveResourcesOnDeletion"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class SyncPolicy implements io.fabric8.kubernetes.api.model.KubernetesResource {

    public enum ApplicationsSync {

        @com.fasterxml.jackson.annotation.JsonProperty("create-only")
        CREATEONLY("create-only"), @com.fasterxml.jackson.annotation.JsonProperty("create-update")
        CREATEUPDATE("create-update"), @com.fasterxml.jackson.annotation.JsonProperty("create-delete")
        CREATEDELETE("create-delete"), @com.fasterxml.jackson.annotation.JsonProperty("sync")
        SYNC("sync");

        java.lang.String value;

        ApplicationsSync(java.lang.String value) {
            this.value = value;
        }
    }

    @com.fasterxml.jackson.annotation.JsonProperty("applicationsSync")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private ApplicationsSync applicationsSync;

    public ApplicationsSync getApplicationsSync() {
        return applicationsSync;
    }

    public void setApplicationsSync(ApplicationsSync applicationsSync) {
        this.applicationsSync = applicationsSync;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("preserveResourcesOnDeletion")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean preserveResourcesOnDeletion;

    public Boolean getPreserveResourcesOnDeletion() {
        return preserveResourcesOnDeletion;
    }

    public void setPreserveResourcesOnDeletion(Boolean preserveResourcesOnDeletion) {
        this.preserveResourcesOnDeletion = preserveResourcesOnDeletion;
    }
}

