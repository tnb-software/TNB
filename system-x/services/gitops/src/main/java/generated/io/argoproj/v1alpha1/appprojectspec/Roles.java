package generated.io.argoproj.v1alpha1.appprojectspec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"groups","jwtTokens","name","policies"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Roles implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Groups are a list of OIDC group claims bound to this role
     */
    @com.fasterxml.jackson.annotation.JsonProperty("groups")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Groups are a list of OIDC group claims bound to this role")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> groups;

    public java.util.List<String> getGroups() {
        return groups;
    }

    public void setGroups(java.util.List<String> groups) {
        this.groups = groups;
    }

    /**
     * JWTTokens are a list of generated JWT tokens bound to this role
     */
    @com.fasterxml.jackson.annotation.JsonProperty("jwtTokens")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("JWTTokens are a list of generated JWT tokens bound to this role")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.roles.JwtTokens> jwtTokens;

    public java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.roles.JwtTokens> getJwtTokens() {
        return jwtTokens;
    }

    public void setJwtTokens(java.util.List<generated.io.argoproj.v1alpha1.appprojectspec.roles.JwtTokens> jwtTokens) {
        this.jwtTokens = jwtTokens;
    }

    /**
     * Name is a name for this role
     */
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Name is a name for this role")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Policies Stores a list of casbin formatted strings that define access policies for the role in the project
     */
    @com.fasterxml.jackson.annotation.JsonProperty("policies")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Policies Stores a list of casbin formatted strings that define access policies for the role in the project")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> policies;

    public java.util.List<String> getPolicies() {
        return policies;
    }

    public void setPolicies(java.util.List<String> policies) {
        this.policies = policies;
    }
}

