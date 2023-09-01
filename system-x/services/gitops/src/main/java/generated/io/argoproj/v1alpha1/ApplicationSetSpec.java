package generated.io.argoproj.v1alpha1;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"applyNestedSelectors","generators","goTemplate","goTemplateOptions","preservedFields","strategy","syncPolicy","template"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
public class ApplicationSetSpec implements io.fabric8.kubernetes.api.model.KubernetesResource {

    @com.fasterxml.jackson.annotation.JsonProperty("applyNestedSelectors")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean applyNestedSelectors;

    public Boolean getApplyNestedSelectors() {
        return applyNestedSelectors;
    }

    public void setApplyNestedSelectors(Boolean applyNestedSelectors) {
        this.applyNestedSelectors = applyNestedSelectors;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("generators")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.Generators> generators;

    public java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.Generators> getGenerators() {
        return generators;
    }

    public void setGenerators(java.util.List<generated.io.argoproj.v1alpha1.applicationsetspec.Generators> generators) {
        this.generators = generators;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("goTemplate")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean goTemplate;

    public Boolean getGoTemplate() {
        return goTemplate;
    }

    public void setGoTemplate(Boolean goTemplate) {
        this.goTemplate = goTemplate;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("goTemplateOptions")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private java.util.List<String> goTemplateOptions;

    public java.util.List<String> getGoTemplateOptions() {
        return goTemplateOptions;
    }

    public void setGoTemplateOptions(java.util.List<String> goTemplateOptions) {
        this.goTemplateOptions = goTemplateOptions;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("preservedFields")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.PreservedFields preservedFields;

    public generated.io.argoproj.v1alpha1.applicationsetspec.PreservedFields getPreservedFields() {
        return preservedFields;
    }

    public void setPreservedFields(generated.io.argoproj.v1alpha1.applicationsetspec.PreservedFields preservedFields) {
        this.preservedFields = preservedFields;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("strategy")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.Strategy strategy;

    public generated.io.argoproj.v1alpha1.applicationsetspec.Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(generated.io.argoproj.v1alpha1.applicationsetspec.Strategy strategy) {
        this.strategy = strategy;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("syncPolicy")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.SyncPolicy syncPolicy;

    public generated.io.argoproj.v1alpha1.applicationsetspec.SyncPolicy getSyncPolicy() {
        return syncPolicy;
    }

    public void setSyncPolicy(generated.io.argoproj.v1alpha1.applicationsetspec.SyncPolicy syncPolicy) {
        this.syncPolicy = syncPolicy;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("template")
    @javax.validation.constraints.NotNull()
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private generated.io.argoproj.v1alpha1.applicationsetspec.Template template;

    public generated.io.argoproj.v1alpha1.applicationsetspec.Template getTemplate() {
        return template;
    }

    public void setTemplate(generated.io.argoproj.v1alpha1.applicationsetspec.Template template) {
        this.template = template;
    }
}

