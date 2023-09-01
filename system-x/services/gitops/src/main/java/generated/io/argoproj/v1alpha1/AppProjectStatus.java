package generated.io.argoproj.v1alpha1;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"jwtTokensByRole"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class AppProjectStatus implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * JWTTokensByRole contains a list of JWT tokens issued for a given role
     */
    @com.fasterxml.jackson.annotation.JsonProperty("jwtTokensByRole")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("JWTTokensByRole contains a list of JWT tokens issued for a given role")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.Map<java.lang.String, generated.io.argoproj.v1alpha1.appprojectstatus.JwtTokensByRole> jwtTokensByRole;

    public java.util.Map<java.lang.String, generated.io.argoproj.v1alpha1.appprojectstatus.JwtTokensByRole> getJwtTokensByRole() {
        return jwtTokensByRole;
    }

    public void setJwtTokensByRole(java.util.Map<java.lang.String, generated.io.argoproj.v1alpha1.appprojectstatus.JwtTokensByRole> jwtTokensByRole) {
        this.jwtTokensByRole = jwtTokensByRole;
    }
}

