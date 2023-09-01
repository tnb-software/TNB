package generated.io.argoproj.v1alpha1.appprojectspec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"keyID"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class SignatureKeys implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * The ID of the key in hexadecimal notation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("keyID")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The ID of the key in hexadecimal notation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String keyID;

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }
}

