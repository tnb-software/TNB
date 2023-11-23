package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"fsGroup","fsGroupChangePolicy","runAsGroup","runAsNonRoot","runAsUser","seLinuxOptions","seccompProfile","supplementalGroups","sysctls","windowsOptions"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class PodSecurityContext implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * A special supplemental group that applies to all containers in a pod. Some volume types allow the Kubelet to change the ownership of that volume to be owned by the pod:
     *  1. The owning GID will be the FSGroup 2. The setgid bit is set (new files created in the volume will be owned by FSGroup) 3. The permission bits are OR'd with rw-rw----
     *  If unset, the Kubelet will not modify the ownership and permissions of any volume. Note that this field cannot be set when spec.os.name is windows.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("fsGroup")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("A special supplemental group that applies to all containers in a pod. Some volume types allow the Kubelet to change the ownership of that volume to be owned by the pod: \n 1. The owning GID will be the FSGroup 2. The setgid bit is set (new files created in the volume will be owned by FSGroup) 3. The permission bits are OR'd with rw-rw---- \n If unset, the Kubelet will not modify the ownership and permissions of any volume. Note that this field cannot be set when spec.os.name is windows.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Long fsGroup;

    public Long getFsGroup() {
        return fsGroup;
    }

    public void setFsGroup(Long fsGroup) {
        this.fsGroup = fsGroup;
    }

    /**
     * fsGroupChangePolicy defines behavior of changing ownership and permission of the volume before being exposed inside Pod. This field will only apply to volume types which support fsGroup based ownership(and permissions). It will have no effect on ephemeral volume types such as: secret, configmaps and emptydir. Valid values are "OnRootMismatch" and "Always". If not specified, "Always" is used. Note that this field cannot be set when spec.os.name is windows.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("fsGroupChangePolicy")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("fsGroupChangePolicy defines behavior of changing ownership and permission of the volume before being exposed inside Pod. This field will only apply to volume types which support fsGroup based ownership(and permissions). It will have no effect on ephemeral volume types such as: secret, configmaps and emptydir. Valid values are \"OnRootMismatch\" and \"Always\". If not specified, \"Always\" is used. Note that this field cannot be set when spec.os.name is windows.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String fsGroupChangePolicy;

    public String getFsGroupChangePolicy() {
        return fsGroupChangePolicy;
    }

    public void setFsGroupChangePolicy(String fsGroupChangePolicy) {
        this.fsGroupChangePolicy = fsGroupChangePolicy;
    }

    /**
     * The GID to run the entrypoint of the container process. Uses runtime default if unset. May also be set in SecurityContext.  If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence for that container. Note that this field cannot be set when spec.os.name is windows.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("runAsGroup")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The GID to run the entrypoint of the container process. Uses runtime default if unset. May also be set in SecurityContext.  If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence for that container. Note that this field cannot be set when spec.os.name is windows.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Long runAsGroup;

    public Long getRunAsGroup() {
        return runAsGroup;
    }

    public void setRunAsGroup(Long runAsGroup) {
        this.runAsGroup = runAsGroup;
    }

    /**
     * Indicates that the container must run as a non-root user. If true, the Kubelet will validate the image at runtime to ensure that it does not run as UID 0 (root) and fail to start the container if it does. If unset or false, no such validation will be performed. May also be set in SecurityContext.  If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("runAsNonRoot")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Indicates that the container must run as a non-root user. If true, the Kubelet will validate the image at runtime to ensure that it does not run as UID 0 (root) and fail to start the container if it does. If unset or false, no such validation will be performed. May also be set in SecurityContext.  If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean runAsNonRoot;

    public Boolean getRunAsNonRoot() {
        return runAsNonRoot;
    }

    public void setRunAsNonRoot(Boolean runAsNonRoot) {
        this.runAsNonRoot = runAsNonRoot;
    }

    /**
     * The UID to run the entrypoint of the container process. Defaults to user specified in image metadata if unspecified. May also be set in SecurityContext.  If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence for that container. Note that this field cannot be set when spec.os.name is windows.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("runAsUser")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The UID to run the entrypoint of the container process. Defaults to user specified in image metadata if unspecified. May also be set in SecurityContext.  If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence for that container. Note that this field cannot be set when spec.os.name is windows.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Long runAsUser;

    public Long getRunAsUser() {
        return runAsUser;
    }

    public void setRunAsUser(Long runAsUser) {
        this.runAsUser = runAsUser;
    }

    /**
     * The SELinux context to be applied to all containers. If unspecified, the container runtime will allocate a random SELinux context for each container.  May also be set in SecurityContext.  If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence for that container. Note that this field cannot be set when spec.os.name is windows.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("seLinuxOptions")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The SELinux context to be applied to all containers. If unspecified, the container runtime will allocate a random SELinux context for each container.  May also be set in SecurityContext.  If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence for that container. Note that this field cannot be set when spec.os.name is windows.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.SeLinuxOptions seLinuxOptions;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.SeLinuxOptions getSeLinuxOptions() {
        return seLinuxOptions;
    }

    public void setSeLinuxOptions(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.SeLinuxOptions seLinuxOptions) {
        this.seLinuxOptions = seLinuxOptions;
    }

    /**
     * The seccomp options to use by the containers in this pod. Note that this field cannot be set when spec.os.name is windows.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("seccompProfile")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The seccomp options to use by the containers in this pod. Note that this field cannot be set when spec.os.name is windows.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.SeccompProfile seccompProfile;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.SeccompProfile getSeccompProfile() {
        return seccompProfile;
    }

    public void setSeccompProfile(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.SeccompProfile seccompProfile) {
        this.seccompProfile = seccompProfile;
    }

    /**
     * A list of groups applied to the first process run in each container, in addition to the container's primary GID.  If unspecified, no groups will be added to any container. Note that this field cannot be set when spec.os.name is windows.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("supplementalGroups")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("A list of groups applied to the first process run in each container, in addition to the container's primary GID.  If unspecified, no groups will be added to any container. Note that this field cannot be set when spec.os.name is windows.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<Long> supplementalGroups;

    public java.util.List<Long> getSupplementalGroups() {
        return supplementalGroups;
    }

    public void setSupplementalGroups(java.util.List<Long> supplementalGroups) {
        this.supplementalGroups = supplementalGroups;
    }

    /**
     * Sysctls hold a list of namespaced sysctls used for the pod. Pods with unsupported sysctls (by the container runtime) might fail to launch. Note that this field cannot be set when spec.os.name is windows.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sysctls")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Sysctls hold a list of namespaced sysctls used for the pod. Pods with unsupported sysctls (by the container runtime) might fail to launch. Note that this field cannot be set when spec.os.name is windows.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.Sysctls> sysctls;

    public java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.Sysctls> getSysctls() {
        return sysctls;
    }

    public void setSysctls(java.util.List<software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.Sysctls> sysctls) {
        this.sysctls = sysctls;
    }

    /**
     * The Windows specific settings applied to all containers. If unspecified, the options within a container's SecurityContext will be used. If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence. Note that this field cannot be set when spec.os.name is linux.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("windowsOptions")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The Windows specific settings applied to all containers. If unspecified, the options within a container's SecurityContext will be used. If set in both SecurityContext and PodSecurityContext, the value specified in SecurityContext takes precedence. Note that this field cannot be set when spec.os.name is linux.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.WindowsOptions windowsOptions;

    public software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.WindowsOptions getWindowsOptions() {
        return windowsOptions;
    }

    public void setWindowsOptions(software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.deploymentplan.podsecuritycontext.WindowsOptions windowsOptions) {
        this.windowsOptions = windowsOptions;
    }
}

