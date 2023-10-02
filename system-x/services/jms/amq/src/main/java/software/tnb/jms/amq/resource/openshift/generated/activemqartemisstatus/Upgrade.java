package software.tnb.jms.amq.resource.openshift.generated.activemqartemisstatus;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"majorUpdates","minorUpdates","patchUpdates","securityUpdates"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class Upgrade implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("majorUpdates")
    
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean majorUpdates;

    public Boolean getMajorUpdates() {
        return majorUpdates;
    }

    public void setMajorUpdates(Boolean majorUpdates) {
        this.majorUpdates = majorUpdates;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("minorUpdates")
    
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean minorUpdates;

    public Boolean getMinorUpdates() {
        return minorUpdates;
    }

    public void setMinorUpdates(Boolean minorUpdates) {
        this.minorUpdates = minorUpdates;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("patchUpdates")
    
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean patchUpdates;

    public Boolean getPatchUpdates() {
        return patchUpdates;
    }

    public void setPatchUpdates(Boolean patchUpdates) {
        this.patchUpdates = patchUpdates;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("securityUpdates")
    
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean securityUpdates;

    public Boolean getSecurityUpdates() {
        return securityUpdates;
    }

    public void setSecurityUpdates(Boolean securityUpdates) {
        this.securityUpdates = securityUpdates;
    }
}

