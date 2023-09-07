package generated.io.argoproj.v1alpha1.appprojectstatus;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"items"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class JwtTokensByRole implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("items")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.appprojectstatus.jwttokensbyrole.Items> items;

    public java.util.List<generated.io.argoproj.v1alpha1.appprojectstatus.jwttokensbyrole.Items> getItems() {
        return items;
    }

    public void setItems(java.util.List<generated.io.argoproj.v1alpha1.appprojectstatus.jwttokensbyrole.Items> items) {
        this.items = items;
    }
}

