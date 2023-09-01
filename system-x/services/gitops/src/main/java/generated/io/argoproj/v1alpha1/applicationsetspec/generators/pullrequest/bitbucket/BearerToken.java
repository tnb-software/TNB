package generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest.bitbucket;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"tokenRef"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class BearerToken implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("tokenRef")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest.bitbucket.bearertoken.TokenRef tokenRef;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest.bitbucket.bearertoken.TokenRef getTokenRef() {
        return tokenRef;
    }

    public void setTokenRef(generated.io.argoproj.v1alpha1.applicationsetspec.generators.pullrequest.bitbucket.bearertoken.TokenRef tokenRef) {
        this.tokenRef = tokenRef;
    }
}

