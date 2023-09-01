package generated.io.argoproj.v1alpha1.applicationsetspec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"rollingSync","type"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Strategy implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("rollingSync")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.strategy.RollingSync rollingSync;

    public generated.io.argoproj.v1alpha1.applicationsetspec.strategy.RollingSync getRollingSync() {
        return rollingSync;
    }

    public void setRollingSync(generated.io.argoproj.v1alpha1.applicationsetspec.strategy.RollingSync rollingSync) {
        this.rollingSync = rollingSync;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

