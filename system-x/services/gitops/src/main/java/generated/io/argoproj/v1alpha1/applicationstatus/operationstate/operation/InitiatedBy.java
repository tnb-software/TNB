package generated.io.argoproj.v1alpha1.applicationstatus.operationstate.operation;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"automated","username"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class InitiatedBy implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Automated is set to true if operation was initiated automatically by the application controller.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("automated")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Automated is set to true if operation was initiated automatically by the application controller.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean automated;

    public Boolean getAutomated() {
        return automated;
    }

    public void setAutomated(Boolean automated) {
        this.automated = automated;
    }

    /**
     * Username contains the name of a user who started operation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("username")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Username contains the name of a user who started operation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

