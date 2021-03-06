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

import java.math.BigDecimal;
import java.util.Objects;

import io.swagger.annotations.ApiModelProperty;

/**
 * TableReportSummaryItem
 */
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-07-12T10:19:43.430893315+02:00[Europe/Rome]")
public class TableReportSummaryItem {
    public static final String SERIALIZED_NAME_ID = "id";
    public static final String SERIALIZED_NAME_CONFIG_ID = "configId";
    public static final String SERIALIZED_NAME_CREATED = "created";
    @SerializedName(SERIALIZED_NAME_ID)
    private Integer id;
    @SerializedName(SERIALIZED_NAME_CONFIG_ID)
    private Integer configId;
    @SerializedName(SERIALIZED_NAME_CREATED)
    private BigDecimal created;

    public TableReportSummaryItem() {
    }

    public TableReportSummaryItem id(Integer id) {

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

    public TableReportSummaryItem configId(Integer configId) {

        this.configId = configId;
        return this;
    }

    /**
     * Get configId
     *
     * @return configId
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public TableReportSummaryItem created(BigDecimal created) {

        this.created = created;
        return this;
    }

    /**
     * Get created
     *
     * @return created
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public BigDecimal getCreated() {
        return created;
    }

    public void setCreated(BigDecimal created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableReportSummaryItem tableReportSummaryItem = (TableReportSummaryItem) o;
        return Objects.equals(this.id, tableReportSummaryItem.id) &&
            Objects.equals(this.configId, tableReportSummaryItem.configId) &&
            Objects.equals(this.created, tableReportSummaryItem.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, configId, created);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TableReportSummaryItem {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    configId: ").append(toIndentedString(configId)).append("\n");
        sb.append("    created: ").append(toIndentedString(created)).append("\n");
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

