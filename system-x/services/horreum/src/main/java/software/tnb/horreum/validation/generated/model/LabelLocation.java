/*
 * Horreum API
 * Horreum data repository API
 *
 * The version of the OpenAPI document: 0.1-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package software.tnb.horreum.validation.generated.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModelProperty;

/**
 * LabelLocation
 */
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-07-12T10:19:43.430893315+02:00[Europe/Rome]")
public class LabelLocation {
    public static final String SERIALIZED_NAME_TYPE = "type";
    public static final String SERIALIZED_NAME_TEST_ID = "testId";
    public static final String SERIALIZED_NAME_TEST_NAME = "testName";
    public static final String SERIALIZED_NAME_RULE_ID = "ruleId";
    public static final String SERIALIZED_NAME_RULE_NAME = "ruleName";
    public static final String SERIALIZED_NAME_CONFIG_ID = "configId";
    public static final String SERIALIZED_NAME_TITLE = "title";
    public static final String SERIALIZED_NAME_WHERE = "where";
    public static final String SERIALIZED_NAME_NAME = "name";
    public static final String SERIALIZED_NAME_VARIABLE_ID = "variableId";
    public static final String SERIALIZED_NAME_VARIABLE_NAME = "variableName";
    public static final String SERIALIZED_NAME_VIEW_ID = "viewId";
    public static final String SERIALIZED_NAME_VIEW_NAME = "viewName";
    public static final String SERIALIZED_NAME_COMPONENT_ID = "componentId";
    public static final String SERIALIZED_NAME_HEADER = "header";
    @SerializedName(SERIALIZED_NAME_TYPE)
    private String type;
    @SerializedName(SERIALIZED_NAME_TEST_ID)
    private Integer testId;
    @SerializedName(SERIALIZED_NAME_TEST_NAME)
    private String testName;
    @SerializedName(SERIALIZED_NAME_RULE_ID)
    private Integer ruleId;
    @SerializedName(SERIALIZED_NAME_RULE_NAME)
    private String ruleName;
    @SerializedName(SERIALIZED_NAME_CONFIG_ID)
    private Integer configId;
    @SerializedName(SERIALIZED_NAME_TITLE)
    private String title;
    @SerializedName(SERIALIZED_NAME_WHERE)
    private String where;
    @SerializedName(SERIALIZED_NAME_NAME)
    private String name;
    @SerializedName(SERIALIZED_NAME_VARIABLE_ID)
    private Integer variableId;
    @SerializedName(SERIALIZED_NAME_VARIABLE_NAME)
    private String variableName;
    @SerializedName(SERIALIZED_NAME_VIEW_ID)
    private Integer viewId;
    @SerializedName(SERIALIZED_NAME_VIEW_NAME)
    private String viewName;
    @SerializedName(SERIALIZED_NAME_COMPONENT_ID)
    private Integer componentId;
    @SerializedName(SERIALIZED_NAME_HEADER)
    private String header;

    public LabelLocation() {
    }

    public LabelLocation type(String type) {

        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LabelLocation testId(Integer testId) {

        this.testId = testId;
        return this;
    }

    /**
     * Get testId
     *
     * @return testId
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public LabelLocation testName(String testName) {

        this.testName = testName;
        return this;
    }

    /**
     * Get testName
     *
     * @return testName
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public LabelLocation ruleId(Integer ruleId) {

        this.ruleId = ruleId;
        return this;
    }

    /**
     * Get ruleId
     *
     * @return ruleId
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public LabelLocation ruleName(String ruleName) {

        this.ruleName = ruleName;
        return this;
    }

    /**
     * Get ruleName
     *
     * @return ruleName
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public LabelLocation configId(Integer configId) {

        this.configId = configId;
        return this;
    }

    /**
     * Get configId
     *
     * @return configId
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public LabelLocation title(String title) {

        this.title = title;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LabelLocation where(String where) {

        this.where = where;
        return this;
    }

    /**
     * Get where
     *
     * @return where
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public LabelLocation name(String name) {

        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LabelLocation variableId(Integer variableId) {

        this.variableId = variableId;
        return this;
    }

    /**
     * Get variableId
     *
     * @return variableId
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getVariableId() {
        return variableId;
    }

    public void setVariableId(Integer variableId) {
        this.variableId = variableId;
    }

    public LabelLocation variableName(String variableName) {

        this.variableName = variableName;
        return this;
    }

    /**
     * Get variableName
     *
     * @return variableName
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public LabelLocation viewId(Integer viewId) {

        this.viewId = viewId;
        return this;
    }

    /**
     * Get viewId
     *
     * @return viewId
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getViewId() {
        return viewId;
    }

    public void setViewId(Integer viewId) {
        this.viewId = viewId;
    }

    public LabelLocation viewName(String viewName) {

        this.viewName = viewName;
        return this;
    }

    /**
     * Get viewName
     *
     * @return viewName
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public LabelLocation componentId(Integer componentId) {

        this.componentId = componentId;
        return this;
    }

    /**
     * Get componentId
     *
     * @return componentId
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
    }

    public LabelLocation header(String header) {

        this.header = header;
        return this;
    }

    /**
     * Get header
     *
     * @return header
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LabelLocation labelLocation = (LabelLocation) o;
        return Objects.equals(this.type, labelLocation.type) &&
            Objects.equals(this.testId, labelLocation.testId) &&
            Objects.equals(this.testName, labelLocation.testName) &&
            Objects.equals(this.ruleId, labelLocation.ruleId) &&
            Objects.equals(this.ruleName, labelLocation.ruleName) &&
            Objects.equals(this.configId, labelLocation.configId) &&
            Objects.equals(this.title, labelLocation.title) &&
            Objects.equals(this.where, labelLocation.where) &&
            Objects.equals(this.name, labelLocation.name) &&
            Objects.equals(this.variableId, labelLocation.variableId) &&
            Objects.equals(this.variableName, labelLocation.variableName) &&
            Objects.equals(this.viewId, labelLocation.viewId) &&
            Objects.equals(this.viewName, labelLocation.viewName) &&
            Objects.equals(this.componentId, labelLocation.componentId) &&
            Objects.equals(this.header, labelLocation.header);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, testId, testName, ruleId, ruleName, configId, title, where, name, variableId, variableName, viewId, viewName,
            componentId, header);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LabelLocation {\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    testId: ").append(toIndentedString(testId)).append("\n");
        sb.append("    testName: ").append(toIndentedString(testName)).append("\n");
        sb.append("    ruleId: ").append(toIndentedString(ruleId)).append("\n");
        sb.append("    ruleName: ").append(toIndentedString(ruleName)).append("\n");
        sb.append("    configId: ").append(toIndentedString(configId)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    where: ").append(toIndentedString(where)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    variableId: ").append(toIndentedString(variableId)).append("\n");
        sb.append("    variableName: ").append(toIndentedString(variableName)).append("\n");
        sb.append("    viewId: ").append(toIndentedString(viewId)).append("\n");
        sb.append("    viewName: ").append(toIndentedString(viewName)).append("\n");
        sb.append("    componentId: ").append(toIndentedString(componentId)).append("\n");
        sb.append("    header: ").append(toIndentedString(header)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

