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
 * Target
 */
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-07-12T10:19:43.430893315+02:00[Europe/Rome]")
public class Target {
    public static final String SERIALIZED_NAME_TARGET = "target";
    public static final String SERIALIZED_NAME_TYPE = "type";
    public static final String SERIALIZED_NAME_REF_ID = "refId";
    public static final String SERIALIZED_NAME_DATA = "data";
    @SerializedName(SERIALIZED_NAME_TARGET)
    private String target;
    @SerializedName(SERIALIZED_NAME_TYPE)
    private String type;
    @SerializedName(SERIALIZED_NAME_REF_ID)
    private String refId;
    @SerializedName(SERIALIZED_NAME_DATA)
    private String data;

    public Target() {
    }

    public Target target(String target) {

        this.target = target;
        return this;
    }

    /**
     * Get target
     *
     * @return target
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Target type(String type) {

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

    public Target refId(String refId) {

        this.refId = refId;
        return this;
    }

    /**
     * Get refId
     *
     * @return refId
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Target data(String data) {

        this.data = data;
        return this;
    }

    /**
     * Get data
     *
     * @return data
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Target target = (Target) o;
        return Objects.equals(this.target, target.target) &&
            Objects.equals(this.type, target.type) &&
            Objects.equals(this.refId, target.refId) &&
            Objects.equals(this.data, target.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, type, refId, data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Target {\n");
        sb.append("    target: ").append(toIndentedString(target)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    refId: ").append(toIndentedString(refId)).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

