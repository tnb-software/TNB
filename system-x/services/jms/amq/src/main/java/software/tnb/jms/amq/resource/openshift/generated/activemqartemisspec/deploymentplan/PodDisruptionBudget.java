package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"maxUnavailable","minAvailable","selector"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class PodDisruptionBudget implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * An eviction is allowed if at most "maxUnavailable" pods selected by "selector" are unavailable after the eviction, i.e. even in absence of the evicted pod. For example, one can prevent all voluntary evictions by specifying 0. This is a mutually exclusive setting with "minAvailable".
     */
    @com.fasterxml.jackson.annotation.JsonProperty("maxUnavailable")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("An eviction is allowed if at most \"maxUnavailable\" pods selected by \"selector\" are unavailable after the eviction, i.e. even in absence of the evicted pod. For example, one can prevent all voluntary evictions by specifying 0. This is a mutually exclusive setting with \"minAvailable\".")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private io.fabric8.kubernetes.api.model.IntOrString maxUnavailable;

    public io.fabric8.kubernetes.api.model.IntOrString getMaxUnavailable() {
        return maxUnavailable;
    }

    public void setMaxUnavailable(io.fabric8.kubernetes.api.model.IntOrString maxUnavailable) {
        this.maxUnavailable = maxUnavailable;
    }

    /**
     * An eviction is allowed if at least "minAvailable" pods selected by "selector" will still be available after the eviction, i.e. even in the absence of the evicted pod.  So for example you can prevent all voluntary evictions by specifying "100%".
     */
    @com.fasterxml.jackson.annotation.JsonProperty("minAvailable")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("An eviction is allowed if at least \"minAvailable\" pods selected by \"selector\" will still be available after the eviction, i.e. even in the absence of the evicted pod.  So for example you can prevent all voluntary evictions by specifying \"100%\".")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private io.fabric8.kubernetes.api.model.IntOrString minAvailable;

    public io.fabric8.kubernetes.api.model.IntOrString getMinAvailable() {
        return minAvailable;
    }

    public void setMinAvailable(io.fabric8.kubernetes.api.model.IntOrString minAvailable) {
        this.minAvailable = minAvailable;
    }

    /**
     * Label query over pods whose evictions are managed by the disruption budget. A null selector will match no pods, while an empty ({}) selector will select all pods within the namespace.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("selector")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Label query over pods whose evictions are managed by the disruption budget. A null selector will match no pods, while an empty ({}) selector will select all pods within the namespace.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.poddisruptionbudget.Selector selector;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.poddisruptionbudget.Selector getSelector() {
        return selector;
    }

    public void setSelector(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.poddisruptionbudget.Selector selector) {
        this.selector = selector;
    }
}

