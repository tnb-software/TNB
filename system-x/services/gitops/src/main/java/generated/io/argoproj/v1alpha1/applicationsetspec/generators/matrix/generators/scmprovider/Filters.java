package generated.io.argoproj.v1alpha1.applicationsetspec.generators.matrix.generators.scmprovider;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"branchMatch","labelMatch","pathsDoNotExist","pathsExist","repositoryMatch"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class Filters implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("branchMatch")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String branchMatch;

    public String getBranchMatch() {
        return branchMatch;
    }

    public void setBranchMatch(String branchMatch) {
        this.branchMatch = branchMatch;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("labelMatch")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String labelMatch;

    public String getLabelMatch() {
        return labelMatch;
    }

    public void setLabelMatch(String labelMatch) {
        this.labelMatch = labelMatch;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("pathsDoNotExist")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> pathsDoNotExist;

    public java.util.List<String> getPathsDoNotExist() {
        return pathsDoNotExist;
    }

    public void setPathsDoNotExist(java.util.List<String> pathsDoNotExist) {
        this.pathsDoNotExist = pathsDoNotExist;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("pathsExist")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> pathsExist;

    public java.util.List<String> getPathsExist() {
        return pathsExist;
    }

    public void setPathsExist(java.util.List<String> pathsExist) {
        this.pathsExist = pathsExist;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("repositoryMatch")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String repositoryMatch;

    public String getRepositoryMatch() {
        return repositoryMatch;
    }

    public void setRepositoryMatch(String repositoryMatch) {
        this.repositoryMatch = repositoryMatch;
    }
}

