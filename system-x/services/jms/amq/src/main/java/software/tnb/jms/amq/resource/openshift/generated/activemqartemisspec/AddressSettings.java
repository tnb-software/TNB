package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"addressSetting","applyRule"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class AddressSettings implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Specifies the address settings
     */
    @com.fasterxml.jackson.annotation.JsonProperty("addressSetting")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Specifies the address settings")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.addresssettings.AddressSetting> addressSetting;

    public java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.addresssettings.AddressSetting> getAddressSetting() {
        return addressSetting;
    }

    public void setAddressSetting(java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.addresssettings.AddressSetting> addressSetting) {
        this.addressSetting = addressSetting;
    }

    /**
     * How to merge the address settings to broker configuration
     */
    @com.fasterxml.jackson.annotation.JsonProperty("applyRule")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("How to merge the address settings to broker configuration")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String applyRule;

    public String getApplyRule() {
        return applyRule;
    }

    public void setApplyRule(String applyRule) {
        this.applyRule = applyRule;
    }
}

