package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"gmsaCredentialSpec","gmsaCredentialSpecName","hostProcess","runAsUserName"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class WindowsOptions implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * GMSACredentialSpec is where the GMSA admission webhook (https://github.com/kubernetes-sigs/windows-gmsa) inlines the contents of the GMSA credential spec named by the GMSACredentialSpecName field.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("gmsaCredentialSpec")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("GMSACredentialSpec is where the GMSA admission webhook (https://github.com/kubernetes-sigs/windows-gmsa) inlines the contents of the GMSA credential spec named by the GMSACredentialSpecName field.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String gmsaCredentialSpec;

    public String getGmsaCredentialSpec() {
        return gmsaCredentialSpec;
    }

    public void setGmsaCredentialSpec(String gmsaCredentialSpec) {
        this.gmsaCredentialSpec = gmsaCredentialSpec;
    }

    /**
     * GMSACredentialSpecName is the name of the GMSA credential spec to use.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("gmsaCredentialSpecName")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("GMSACredentialSpecName is the name of the GMSA credential spec to use.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String gmsaCredentialSpecName;

    public String getGmsaCredentialSpecName() {
        return gmsaCredentialSpecName;
    }

    public void setGmsaCredentialSpecName(String gmsaCredentialSpecName) {
        this.gmsaCredentialSpecName = gmsaCredentialSpecName;
    }

    /**
     * HostProcess determines if a container should be run as a 'Host Process' container. This field is alpha-level and will only be honored by components that enable the WindowsHostProcessContainers feature flag. Setting this field without the feature flag will result in errors when validating the Pod. All of a Pod's containers must have the same effective HostProcess value (it is not allowed to have a mix of HostProcess containers and non-HostProcess containers).  In addition, if HostProcess is true then HostNetwork must also be set to true.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("hostProcess")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("HostProcess determines if a container should be run as a 'Host Process' container. This field is alpha-level and will only be honored by components that enable the WindowsHostProcessContainers feature flag. Setting this field without the feature flag will result in errors when validating the Pod. All of a Pod's containers must have the same effective HostProcess value (it is not allowed to have a mix of HostProcess containers and non-HostProcess containers).  In addition, if HostProcess is true then HostNetwork must also be set to true.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean hostProcess;

    public Boolean getHostProcess() {
        return hostProcess;
    }

    public void setHostProcess(Boolean hostProcess) {
        this.hostProcess = hostProcess;
    }

    /**
     * The UserName in Windows to run the entrypoint of the container process. Defaults to the user specified in image metadata if unspecified. May also be set in PodSecurityContext. If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("runAsUserName")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The UserName in Windows to run the entrypoint of the container process. Defaults to the user specified in image metadata if unspecified. May also be set in PodSecurityContext. If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String runAsUserName;

    public String getRunAsUserName() {
        return runAsUserName;
    }

    public void setRunAsUserName(String runAsUserName) {
        this.runAsUserName = runAsUserName;
    }
}

