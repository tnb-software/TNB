package generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.pullrequest.bitbucket;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"passwordRef","username"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class BasicAuth implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("passwordRef")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.pullrequest.bitbucket.basicauth.PasswordRef passwordRef;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.pullrequest.bitbucket.basicauth.PasswordRef getPasswordRef() {
        return passwordRef;
    }

    public void setPasswordRef(generated.io.argoproj.v1alpha1.applicationsetspec.generators.merge.generators.pullrequest.bitbucket.basicauth.PasswordRef passwordRef) {
        this.passwordRef = passwordRef;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

