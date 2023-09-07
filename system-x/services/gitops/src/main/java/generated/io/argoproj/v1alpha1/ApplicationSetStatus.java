package generated.io.argoproj.v1alpha1;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"applicationStatus","conditions"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class ApplicationSetStatus implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("applicationStatus")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetstatus.ApplicationStatus> applicationStatus;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetstatus.ApplicationStatus> getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(java.util.List<generated.io.argoproj.v1alpha1.applicationsetstatus.ApplicationStatus> applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("conditions")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetstatus.Conditions> conditions;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetstatus.Conditions> getConditions() {
        return conditions;
    }

    public void setConditions(java.util.List<generated.io.argoproj.v1alpha1.applicationsetstatus.Conditions> conditions) {
        this.conditions = conditions;
    }
}

