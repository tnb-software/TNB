package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.env.valuefrom;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"containerName","divisor","resource"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class ResourceFieldRef implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Container name: required for volumes, optional for env vars
     */
    @com.fasterxml.jackson.annotation.JsonProperty("containerName")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Container name: required for volumes, optional for env vars")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String containerName;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    /**
     * Specifies the output format of the exposed resources, defaults to "1"
     */
    @com.fasterxml.jackson.annotation.JsonProperty("divisor")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the output format of the exposed resources, defaults to \"1\"")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private io.fabric8.kubernetes.api.model.IntOrString divisor;

    public io.fabric8.kubernetes.api.model.IntOrString getDivisor() {
        return divisor;
    }

    public void setDivisor(io.fabric8.kubernetes.api.model.IntOrString divisor) {
        this.divisor = divisor;
    }

    /**
     * Required: resource to select
     */
    @com.fasterxml.jackson.annotation.JsonProperty("resource")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Required: resource to select")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String resource;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}

