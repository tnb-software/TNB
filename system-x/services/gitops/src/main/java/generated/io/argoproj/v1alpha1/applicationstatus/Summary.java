package generated.io.argoproj.v1alpha1.applicationstatus;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"externalURLs","images"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Summary implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * ExternalURLs holds all external URLs of application child resources.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("externalURLs")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("ExternalURLs holds all external URLs of application child resources.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> externalURLs;

    public java.util.List<String> getExternalURLs() {
        return externalURLs;
    }

    public void setExternalURLs(java.util.List<String> externalURLs) {
        this.externalURLs = externalURLs;
    }

    /**
     * Images holds all images of application child resources.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("images")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Images holds all images of application child resources.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> images;

    public java.util.List<String> getImages() {
        return images;
    }

    public void setImages(java.util.List<String> images) {
        this.images = images;
    }
}

