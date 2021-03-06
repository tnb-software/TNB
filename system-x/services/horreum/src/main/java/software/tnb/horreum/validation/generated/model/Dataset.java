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

import org.threeten.bp.OffsetDateTime;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModelProperty;

/**
 * Dataset
 */
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-07-12T10:19:43.430893315+02:00[Europe/Rome]")
public class Dataset {
    public static final String SERIALIZED_NAME_OWNER = "owner";
    public static final String SERIALIZED_NAME_ACCESS = "access";
    public static final String SERIALIZED_NAME_ID = "id";
    public static final String SERIALIZED_NAME_START = "start";
    public static final String SERIALIZED_NAME_STOP = "stop";
    public static final String SERIALIZED_NAME_DESCRIPTION = "description";
    public static final String SERIALIZED_NAME_TESTID = "testid";
    public static final String SERIALIZED_NAME_DATA = "data";
    public static final String SERIALIZED_NAME_ORDINAL = "ordinal";
    @SerializedName(SERIALIZED_NAME_OWNER)
    private String owner;
    @SerializedName(SERIALIZED_NAME_ACCESS)
    private Access access;
    @SerializedName(SERIALIZED_NAME_ID)
    private Integer id;
    @SerializedName(SERIALIZED_NAME_START)
    private OffsetDateTime start;
    @SerializedName(SERIALIZED_NAME_STOP)
    private OffsetDateTime stop;
    @SerializedName(SERIALIZED_NAME_DESCRIPTION)
    private String description;
    @SerializedName(SERIALIZED_NAME_TESTID)
    private Integer testid;
    @SerializedName(SERIALIZED_NAME_DATA)
    private List data = null;
    @SerializedName(SERIALIZED_NAME_ORDINAL)
    private Integer ordinal;

    public Dataset() {
    }

    public Dataset owner(String owner) {

        this.owner = owner;
        return this;
    }

    /**
     * Get owner
     *
     * @return owner
     **/
    @javax.annotation.Nonnull
    @ApiModelProperty(required = true, value = "")

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Dataset access(Access access) {

        this.access = access;
        return this;
    }

    /**
     * Get access
     *
     * @return access
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(required = true, value = "")

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Dataset id(Integer id) {

        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Dataset start(OffsetDateTime start) {

        this.start = start;
        return this;
    }

    /**
     * Get start
     *
     * @return start
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public OffsetDateTime getStart() {
        return start;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    public Dataset stop(OffsetDateTime stop) {

        this.stop = stop;
        return this;
    }

    /**
     * Get stop
     *
     * @return stop
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public OffsetDateTime getStop() {
        return stop;
    }

    public void setStop(OffsetDateTime stop) {
        this.stop = stop;
    }

    public Dataset description(String description) {

        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Dataset testid(Integer testid) {

        this.testid = testid;
        return this;
    }

    /**
     * Get testid
     *
     * @return testid
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getTestid() {
        return testid;
    }

    public void setTestid(Integer testid) {
        this.testid = testid;
    }

    public Dataset data(List data) {

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

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public Dataset ordinal(Integer ordinal) {

        this.ordinal = ordinal;
        return this;
    }

    /**
     * Get ordinal
     *
     * @return ordinal
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dataset dataset = (Dataset) o;
        return Objects.equals(this.owner, dataset.owner) &&
            Objects.equals(this.access, dataset.access) &&
            Objects.equals(this.id, dataset.id) &&
            Objects.equals(this.start, dataset.start) &&
            Objects.equals(this.stop, dataset.stop) &&
            Objects.equals(this.description, dataset.description) &&
            Objects.equals(this.testid, dataset.testid) &&
            Objects.equals(this.data, dataset.data) &&
            Objects.equals(this.ordinal, dataset.ordinal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, access, id, start, stop, description, testid, data, ordinal);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Dataset {\n");
        sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
        sb.append("    access: ").append(toIndentedString(access)).append("\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    start: ").append(toIndentedString(start)).append("\n");
        sb.append("    stop: ").append(toIndentedString(stop)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    testid: ").append(toIndentedString(testid)).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
        sb.append("    ordinal: ").append(toIndentedString(ordinal)).append("\n");
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

