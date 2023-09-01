package generated.io.argoproj.v1alpha1.applicationstatus.sync.comparedto;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"group","jqPathExpressions","jsonPointers","kind","managedFieldsManagers","name","namespace"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class IgnoreDifferences implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("group")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("jqPathExpressions")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> jqPathExpressions;

    public java.util.List<String> getJqPathExpressions() {
        return jqPathExpressions;
    }

    public void setJqPathExpressions(java.util.List<String> jqPathExpressions) {
        this.jqPathExpressions = jqPathExpressions;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("jsonPointers")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> jsonPointers;

    public java.util.List<String> getJsonPointers() {
        return jsonPointers;
    }

    public void setJsonPointers(java.util.List<String> jsonPointers) {
        this.jsonPointers = jsonPointers;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("kind")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String kind;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * ManagedFieldsManagers is a list of trusted managers. Fields mutated by those managers will take precedence over the desired state defined in the SCM and won't be displayed in diffs
     */
    @com.fasterxml.jackson.annotation.JsonProperty("managedFieldsManagers")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ManagedFieldsManagers is a list of trusted managers. Fields mutated by those managers will take precedence over the desired state defined in the SCM and won't be displayed in diffs")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> managedFieldsManagers;

    public java.util.List<String> getManagedFieldsManagers() {
        return managedFieldsManagers;
    }

    public void setManagedFieldsManagers(java.util.List<String> managedFieldsManagers) {
        this.managedFieldsManagers = managedFieldsManagers;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("namespace")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String namespace;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}

