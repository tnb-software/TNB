package generated.io.argoproj.v1alpha1.applicationstatus.operationstate.syncresult.sources.helm;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"forceString","name","value"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Parameters implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * ForceString determines whether to tell Helm to interpret booleans and numbers as strings
     */
    @com.fasterxml.jackson.annotation.JsonProperty("forceString")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ForceString determines whether to tell Helm to interpret booleans and numbers as strings")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean forceString;

    public Boolean getForceString() {
        return forceString;
    }

    public void setForceString(Boolean forceString) {
        this.forceString = forceString;
    }

    /**
     * Name is the name of the Helm parameter
     */
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Name is the name of the Helm parameter")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Value is the value for the Helm parameter
     */
    @com.fasterxml.jackson.annotation.JsonProperty("value")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Value is the value for the Helm parameter")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

