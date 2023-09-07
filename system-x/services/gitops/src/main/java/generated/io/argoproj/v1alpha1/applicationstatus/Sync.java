package generated.io.argoproj.v1alpha1.applicationstatus;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"comparedTo","revision","revisions","status"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Sync implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * ComparedTo contains information about what has been compared
     */
    @com.fasterxml.jackson.annotation.JsonProperty("comparedTo")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ComparedTo contains information about what has been compared")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationstatus.sync.ComparedTo comparedTo;

    public generated.io.argoproj.v1alpha1.applicationstatus.sync.ComparedTo getComparedTo() {
        return comparedTo;
    }

    public void setComparedTo(generated.io.argoproj.v1alpha1.applicationstatus.sync.ComparedTo comparedTo) {
        this.comparedTo = comparedTo;
    }

    /**
     * Revision contains information about the revision the comparison has been performed to
     */
    @com.fasterxml.jackson.annotation.JsonProperty("revision")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Revision contains information about the revision the comparison has been performed to")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String revision;

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * Revisions contains information about the revisions of multiple sources the comparison has been performed to
     */
    @com.fasterxml.jackson.annotation.JsonProperty("revisions")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Revisions contains information about the revisions of multiple sources the comparison has been performed to")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> revisions;

    public java.util.List<String> getRevisions() {
        return revisions;
    }

    public void setRevisions(java.util.List<String> revisions) {
        this.revisions = revisions;
    }

    /**
     * Status is the sync state of the comparison
     */
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Status is the sync state of the comparison")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

