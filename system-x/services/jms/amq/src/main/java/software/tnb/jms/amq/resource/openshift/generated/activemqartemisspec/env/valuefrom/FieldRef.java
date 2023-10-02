package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.env.valuefrom;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"apiVersion","fieldPath"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class FieldRef implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * Version of the schema the FieldPath is written in terms of, defaults to "v1".
     */
    @com.fasterxml.jackson.annotation.JsonProperty("apiVersion")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Version of the schema the FieldPath is written in terms of, defaults to \"v1\".")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String apiVersion;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * Path of the field to select in the specified API version.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("fieldPath")
    
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Path of the field to select in the specified API version.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String fieldPath;

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }
}

