package software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"ready","starting","stopped"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class PodStatus implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Deployments are ready to serve requests
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ready")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Deployments are ready to serve requests")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> ready;

    public java.util.List<String> getReady() {
        return ready;
    }

    public void setReady(java.util.List<String> ready) {
        this.ready = ready;
    }

    /**
     * Deployments are starting, may or may not succeed
     */
    @com.fasterxml.jackson.annotation.JsonProperty("starting")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Deployments are starting, may or may not succeed")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> starting;

    public java.util.List<String> getStarting() {
        return starting;
    }

    public void setStarting(java.util.List<String> starting) {
        this.starting = starting;
    }

    /**
     * Deployments are not starting, unclear what next step will be
     */
    @com.fasterxml.jackson.annotation.JsonProperty("stopped")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Deployments are not starting, unclear what next step will be")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> stopped;

    public java.util.List<String> getStopped() {
        return stopped;
    }

    public void setStopped(java.util.List<String> stopped) {
        this.stopped = stopped;
    }
}

