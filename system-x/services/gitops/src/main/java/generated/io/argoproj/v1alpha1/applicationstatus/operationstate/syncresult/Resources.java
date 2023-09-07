package generated.io.argoproj.v1alpha1.applicationstatus.operationstate.syncresult;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"group","hookPhase","hookType","kind","message","name","namespace","status","syncPhase","version"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Resources implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Group specifies the API group of the resource
     */
    @com.fasterxml.jackson.annotation.JsonProperty("group")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Group specifies the API group of the resource")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * HookPhase contains the state of any operation associated with this resource OR hook This can also contain values for non-hook resources.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("hookPhase")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("HookPhase contains the state of any operation associated with this resource OR hook This can also contain values for non-hook resources.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String hookPhase;

    public String getHookPhase() {
        return hookPhase;
    }

    public void setHookPhase(String hookPhase) {
        this.hookPhase = hookPhase;
    }

    /**
     * HookType specifies the type of the hook. Empty for non-hook resources
     */
    @com.fasterxml.jackson.annotation.JsonProperty("hookType")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("HookType specifies the type of the hook. Empty for non-hook resources")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String hookType;

    public String getHookType() {
        return hookType;
    }

    public void setHookType(String hookType) {
        this.hookType = hookType;
    }

    /**
     * Kind specifies the API kind of the resource
     */
    @com.fasterxml.jackson.annotation.JsonProperty("kind")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Kind specifies the API kind of the resource")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String kind;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * Message contains an informational or error message for the last sync OR operation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("message")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Message contains an informational or error message for the last sync OR operation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Name specifies the name of the resource
     */
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Name specifies the name of the resource")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Namespace specifies the target namespace of the resource
     */
    @com.fasterxml.jackson.annotation.JsonProperty("namespace")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Namespace specifies the target namespace of the resource")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String namespace;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Status holds the final result of the sync. Will be empty if the resources is yet to be applied/pruned and is always zero-value for hooks
     */
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Status holds the final result of the sync. Will be empty if the resources is yet to be applied/pruned and is always zero-value for hooks")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * SyncPhase indicates the particular phase of the sync that this result was acquired in
     */
    @com.fasterxml.jackson.annotation.JsonProperty("syncPhase")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("SyncPhase indicates the particular phase of the sync that this result was acquired in")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String syncPhase;

    public String getSyncPhase() {
        return syncPhase;
    }

    public void setSyncPhase(String syncPhase) {
        this.syncPhase = syncPhase;
    }

    /**
     * Version specifies the API version of the resource
     */
    @com.fasterxml.jackson.annotation.JsonProperty("version")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Version specifies the API version of the resource")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

