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
 * LabelInfo
 */
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-07-12T10:19:43.430893315+02:00[Europe/Rome]")
public class LabelInfo {
    public static final String SERIALIZED_NAME_NAME = "name";
    public static final String SERIALIZED_NAME_METRICS = "metrics";
    public static final String SERIALIZED_NAME_FILTERING = "filtering";
    public static final String SERIALIZED_NAME_SCHEMAS = "schemas";
    @SerializedName(SERIALIZED_NAME_NAME)
    private String name;
    @SerializedName(SERIALIZED_NAME_METRICS)
    private Boolean metrics;
    @SerializedName(SERIALIZED_NAME_FILTERING)
    private Boolean filtering;
    @SerializedName(SERIALIZED_NAME_SCHEMAS)
    private List<SchemaDescriptor> schemas = new ArrayList<SchemaDescriptor>();

    public LabelInfo() {
    }

    public LabelInfo name(String name) {

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

    public LabelInfo metrics(Boolean metrics) {

        this.metrics = metrics;
        return this;
    }

    /**
     * Get metrics
     *
     * @return metrics
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public Boolean getMetrics() {
        return metrics;
    }

    public void setMetrics(Boolean metrics) {
        this.metrics = metrics;
    }

    public LabelInfo filtering(Boolean filtering) {

        this.filtering = filtering;
        return this;
    }

    /**
     * Get filtering
     *
     * @return filtering
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public Boolean getFiltering() {
        return filtering;
    }

    public void setFiltering(Boolean filtering) {
        this.filtering = filtering;
    }

    public LabelInfo schemas(List<SchemaDescriptor> schemas) {

        this.schemas = schemas;
        return this;
    }

    public LabelInfo addSchemasItem(SchemaDescriptor schemasItem) {
        this.schemas.add(schemasItem);
        return this;
    }

    /**
     * Get schemas
     *
     * @return schemas
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public List<SchemaDescriptor> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<SchemaDescriptor> schemas) {
        this.schemas = schemas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LabelInfo labelInfo = (LabelInfo) o;
        return Objects.equals(this.name, labelInfo.name) &&
            Objects.equals(this.metrics, labelInfo.metrics) &&
            Objects.equals(this.filtering, labelInfo.filtering) &&
            Objects.equals(this.schemas, labelInfo.schemas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, metrics, filtering, schemas);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LabelInfo {\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    metrics: ").append(toIndentedString(metrics)).append("\n");
        sb.append("    filtering: ").append(toIndentedString(filtering)).append("\n");
        sb.append("    schemas: ").append(toIndentedString(schemas)).append("\n");
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

