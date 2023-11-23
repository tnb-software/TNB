package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"labelSelector","namespaceSelector","namespaces","topologyKey"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class RequiredDuringSchedulingIgnoredDuringExecution implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * A label query over a set of resources, in this case pods.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("labelSelector")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("A label query over a set of resources, in this case pods.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity.requiredduringschedulingignoredduringexecution.LabelSelector labelSelector;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity.requiredduringschedulingignoredduringexecution.LabelSelector getLabelSelector() {
        return labelSelector;
    }

    public void setLabelSelector(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity.requiredduringschedulingignoredduringexecution.LabelSelector labelSelector) {
        this.labelSelector = labelSelector;
    }

    /**
     * A label query over the set of namespaces that the term applies to. The term is applied to the union of the namespaces selected by this field and the ones listed in the namespaces field. null selector and null or empty namespaces list means "this pod's namespace". An empty selector ({}) matches all namespaces. This field is beta-level and is only honored when PodAffinityNamespaceSelector feature is enabled.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("namespaceSelector")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("A label query over the set of namespaces that the term applies to. The term is applied to the union of the namespaces selected by this field and the ones listed in the namespaces field. null selector and null or empty namespaces list means \"this pod's namespace\". An empty selector ({}) matches all namespaces. This field is beta-level and is only honored when PodAffinityNamespaceSelector feature is enabled.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity.requiredduringschedulingignoredduringexecution.NamespaceSelector namespaceSelector;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity.requiredduringschedulingignoredduringexecution.NamespaceSelector getNamespaceSelector() {
        return namespaceSelector;
    }

    public void setNamespaceSelector(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.affinity.podantiaffinity.requiredduringschedulingignoredduringexecution.NamespaceSelector namespaceSelector) {
        this.namespaceSelector = namespaceSelector;
    }

    /**
     * namespaces specifies a static list of namespace names that the term applies to. The term is applied to the union of the namespaces listed in this field and the ones selected by namespaceSelector. null or empty namespaces list and null namespaceSelector means "this pod's namespace"
     */
    @com.fasterxml.jackson.annotation.JsonProperty("namespaces")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("namespaces specifies a static list of namespace names that the term applies to. The term is applied to the union of the namespaces listed in this field and the ones selected by namespaceSelector. null or empty namespaces list and null namespaceSelector means \"this pod's namespace\"")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> namespaces;

    public java.util.List<String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(java.util.List<String> namespaces) {
        this.namespaces = namespaces;
    }

    /**
     * This pod should be co-located (affinity) or not co-located (anti-affinity) with the pods matching the labelSelector in the specified namespaces, where co-located is defined as running on a node whose value of the label with key topologyKey matches that of any node on which any of the selected pods is running. Empty topologyKey is not allowed.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("topologyKey")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("This pod should be co-located (affinity) or not co-located (anti-affinity) with the pods matching the labelSelector in the specified namespaces, where co-located is defined as running on a node whose value of the label with key topologyKey matches that of any node on which any of the selected pods is running. Empty topologyKey is not allowed.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String topologyKey;

    public String getTopologyKey() {
        return topologyKey;
    }

    public void setTopologyKey(String topologyKey) {
        this.topologyKey = topologyKey;
    }
}

