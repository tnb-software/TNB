package generated.io.argoproj.v1alpha1.appprojectspec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"applications","clusters","duration","kind","manualSync","namespaces","schedule","timeZone"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class SyncWindows implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Applications contains a list of applications that the window will apply to
     */
    @com.fasterxml.jackson.annotation.JsonProperty("applications")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Applications contains a list of applications that the window will apply to")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> applications;

    public java.util.List<String> getApplications() {
        return applications;
    }

    public void setApplications(java.util.List<String> applications) {
        this.applications = applications;
    }

    /**
     * Clusters contains a list of clusters that the window will apply to
     */
    @com.fasterxml.jackson.annotation.JsonProperty("clusters")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Clusters contains a list of clusters that the window will apply to")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> clusters;

    public java.util.List<String> getClusters() {
        return clusters;
    }

    public void setClusters(java.util.List<String> clusters) {
        this.clusters = clusters;
    }

    /**
     * Duration is the amount of time the sync window will be open
     */
    @com.fasterxml.jackson.annotation.JsonProperty("duration")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Duration is the amount of time the sync window will be open")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Kind defines if the window allows or blocks syncs
     */
    @com.fasterxml.jackson.annotation.JsonProperty("kind")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Kind defines if the window allows or blocks syncs")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String kind;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * ManualSync enables manual syncs when they would otherwise be blocked
     */
    @com.fasterxml.jackson.annotation.JsonProperty("manualSync")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ManualSync enables manual syncs when they would otherwise be blocked")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean manualSync;

    public Boolean getManualSync() {
        return manualSync;
    }

    public void setManualSync(Boolean manualSync) {
        this.manualSync = manualSync;
    }

    /**
     * Namespaces contains a list of namespaces that the window will apply to
     */
    @com.fasterxml.jackson.annotation.JsonProperty("namespaces")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Namespaces contains a list of namespaces that the window will apply to")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> namespaces;

    public java.util.List<String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(java.util.List<String> namespaces) {
        this.namespaces = namespaces;
    }

    /**
     * Schedule is the time the window will begin, specified in cron format
     */
    @com.fasterxml.jackson.annotation.JsonProperty("schedule")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Schedule is the time the window will begin, specified in cron format")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String schedule;

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    /**
     * TimeZone of the sync that will be applied to the schedule
     */
    @com.fasterxml.jackson.annotation.JsonProperty("timeZone")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("TimeZone of the sync that will be applied to the schedule")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String timeZone;

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}

