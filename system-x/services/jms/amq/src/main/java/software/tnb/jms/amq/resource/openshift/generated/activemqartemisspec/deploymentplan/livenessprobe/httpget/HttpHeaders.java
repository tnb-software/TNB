package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.livenessprobe.httpget;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"name","value"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class HttpHeaders implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * The header field name
     */
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The header field name")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The header field value
     */
    @com.fasterxml.jackson.annotation.JsonProperty("value")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The header field value")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

