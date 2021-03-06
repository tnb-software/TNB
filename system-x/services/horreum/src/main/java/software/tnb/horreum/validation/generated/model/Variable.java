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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModelProperty;

/**
 * Variable
 */
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-07-12T10:19:43.430893315+02:00[Europe/Rome]")
public class Variable {
    public static final String SERIALIZED_NAME_ID = "id";
    public static final String SERIALIZED_NAME_TEST_ID = "testId";
    public static final String SERIALIZED_NAME_NAME = "name";
    public static final String SERIALIZED_NAME_GROUP = "group";
    public static final String SERIALIZED_NAME_ORDER = "order";
    public static final String SERIALIZED_NAME_LABELS = "labels";
    public static final String SERIALIZED_NAME_CALCULATION = "calculation";
    public static final String SERIALIZED_NAME_CHANGE_DETECTION = "changeDetection";
    @SerializedName(SERIALIZED_NAME_ID)
    private Integer id;
    @SerializedName(SERIALIZED_NAME_TEST_ID)
    private Integer testId;
    @SerializedName(SERIALIZED_NAME_NAME)
    private String name;
    @SerializedName(SERIALIZED_NAME_GROUP)
    private String group;
    @SerializedName(SERIALIZED_NAME_ORDER)
    private Integer order;
    @SerializedName(SERIALIZED_NAME_LABELS)
    private List labels;
    @SerializedName(SERIALIZED_NAME_CALCULATION)
    private String calculation;
    @SerializedName(SERIALIZED_NAME_CHANGE_DETECTION)
    private List<ChangeDetection> changeDetection = new ArrayList<ChangeDetection>();

    public Variable() {
    }

    public Variable id(Integer id) {

        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Variable testId(Integer testId) {

        this.testId = testId;
        return this;
    }

    /**
     * Get testId
     *
     * @return testId
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Variable name(String name) {

        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Variable group(String group) {

        this.group = group;
        return this;
    }

    /**
     * Get group
     *
     * @return group
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Variable order(Integer order) {

        this.order = order;
        return this;
    }

    /**
     * Get order
     *
     * @return order
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Variable labels(List labels) {

        this.labels = labels;
        return this;
    }

    /**
     * Get labels
     *
     * @return labels
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(required = true, value = "")

    public List getLabels() {
        return labels;
    }

    public void setLabels(List labels) {
        this.labels = labels;
    }

    public Variable calculation(String calculation) {

        this.calculation = calculation;
        return this;
    }

    /**
     * Get calculation
     *
     * @return calculation
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getCalculation() {
        return calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    public Variable changeDetection(List<ChangeDetection> changeDetection) {

        this.changeDetection = changeDetection;
        return this;
    }

    public Variable addChangeDetectionItem(ChangeDetection changeDetectionItem) {
        this.changeDetection.add(changeDetectionItem);
        return this;
    }

    /**
     * Get changeDetection
     *
     * @return changeDetection
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public List<ChangeDetection> getChangeDetection() {
        return changeDetection;
    }

    public void setChangeDetection(List<ChangeDetection> changeDetection) {
        this.changeDetection = changeDetection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Variable variable = (Variable) o;
        return Objects.equals(this.id, variable.id) &&
            Objects.equals(this.testId, variable.testId) &&
            Objects.equals(this.name, variable.name) &&
            Objects.equals(this.group, variable.group) &&
            Objects.equals(this.order, variable.order) &&
            Objects.equals(this.labels, variable.labels) &&
            Objects.equals(this.calculation, variable.calculation) &&
            Objects.equals(this.changeDetection, variable.changeDetection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, testId, name, group, order, labels, calculation, changeDetection);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Variable {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    testId: ").append(toIndentedString(testId)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    group: ").append(toIndentedString(group)).append("\n");
        sb.append("    order: ").append(toIndentedString(order)).append("\n");
        sb.append("    labels: ").append(toIndentedString(labels)).append("\n");
        sb.append("    calculation: ").append(toIndentedString(calculation)).append("\n");
        sb.append("    changeDetection: ").append(toIndentedString(changeDetection)).append("\n");
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

