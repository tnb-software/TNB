package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"podAffinityTerm","weight"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class PreferredDuringSchedulingIgnoredDuringExecution implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Required. A pod affinity term, associated with the corresponding weight.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("podAffinityTerm")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Required. A pod affinity term, associated with the corresponding weight.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity.preferredduringschedulingignoredduringexecution.PodAffinityTerm podAffinityTerm;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity.preferredduringschedulingignoredduringexecution.PodAffinityTerm getPodAffinityTerm() {
        return podAffinityTerm;
    }

    public void setPodAffinityTerm(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity.preferredduringschedulingignoredduringexecution.PodAffinityTerm podAffinityTerm) {
        this.podAffinityTerm = podAffinityTerm;
    }

    /**
     * weight associated with matching the corresponding podAffinityTerm, in the range 1-100.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("weight")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("weight associated with matching the corresponding podAffinityTerm, in the range 1-100.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer weight;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}

