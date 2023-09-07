package generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"allBranches","appPasswordRef","owner","user"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Bitbucket implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("allBranches")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean allBranches;

    public Boolean getAllBranches() {
        return allBranches;
    }

    public void setAllBranches(Boolean allBranches) {
        this.allBranches = allBranches;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("appPasswordRef")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider.bitbucket.AppPasswordRef appPasswordRef;

    public generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider.bitbucket.AppPasswordRef getAppPasswordRef() {
        return appPasswordRef;
    }

    public void setAppPasswordRef(generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider.bitbucket.AppPasswordRef appPasswordRef) {
        this.appPasswordRef = appPasswordRef;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("owner")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

