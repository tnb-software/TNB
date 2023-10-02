package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.livenessprobe;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"host","port"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class TcpSocket implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Optional: Host name to connect to, defaults to the pod IP.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("host")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Optional: Host name to connect to, defaults to the pod IP.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Number or name of the port to access on the container. Number must be in the range 1 to 65535. Name must be an IANA_SVC_NAME.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("port")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Number or name of the port to access on the container. Number must be in the range 1 to 65535. Name must be an IANA_SVC_NAME.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private io.fabric8.kubernetes.api.model.IntOrString port;

    public io.fabric8.kubernetes.api.model.IntOrString getPort() {
        return port;
    }

    public void setPort(io.fabric8.kubernetes.api.model.IntOrString port) {
        this.port = port;
    }
}

