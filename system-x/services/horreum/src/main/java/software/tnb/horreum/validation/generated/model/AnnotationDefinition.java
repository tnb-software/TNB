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
 * AnnotationDefinition
 */
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-07-12T10:19:43.430893315+02:00[Europe/Rome]")
public class AnnotationDefinition {
    public static final String SERIALIZED_NAME_TITLE = "title";
    public static final String SERIALIZED_NAME_TEXT = "text";
    public static final String SERIALIZED_NAME_IS_REGION = "isRegion";
    public static final String SERIALIZED_NAME_TIME = "time";
    public static final String SERIALIZED_NAME_TIME_END = "timeEnd";
    public static final String SERIALIZED_NAME_TAGS = "tags";
    public static final String SERIALIZED_NAME_CHANGE_ID = "changeId";
    public static final String SERIALIZED_NAME_VARIABLE_ID = "variableId";
    public static final String SERIALIZED_NAME_RUN_ID = "runId";
    public static final String SERIALIZED_NAME_DATASET_ORDINAL = "datasetOrdinal";
    @SerializedName(SERIALIZED_NAME_TITLE)
    private String title;
    @SerializedName(SERIALIZED_NAME_TEXT)
    private String text;
    @SerializedName(SERIALIZED_NAME_IS_REGION)
    private Boolean isRegion;
    @SerializedName(SERIALIZED_NAME_TIME)
    private Long time;
    @SerializedName(SERIALIZED_NAME_TIME_END)
    private Long timeEnd;
    @SerializedName(SERIALIZED_NAME_TAGS)
    private List<String> tags = null;
    @SerializedName(SERIALIZED_NAME_CHANGE_ID)
    private Integer changeId;
    @SerializedName(SERIALIZED_NAME_VARIABLE_ID)
    private Integer variableId;
    @SerializedName(SERIALIZED_NAME_RUN_ID)
    private Integer runId;
    @SerializedName(SERIALIZED_NAME_DATASET_ORDINAL)
    private Integer datasetOrdinal;

    public AnnotationDefinition() {
    }

    public AnnotationDefinition title(String title) {

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

    public AnnotationDefinition text(String text) {

        this.text = text;
        return this;
    }

    /**
     * Get text
     *
     * @return text
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AnnotationDefinition isRegion(Boolean isRegion) {

        this.isRegion = isRegion;
        return this;
    }

    /**
     * Get isRegion
     *
     * @return isRegion
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Boolean getIsRegion() {
        return isRegion;
    }

    public void setIsRegion(Boolean isRegion) {
        this.isRegion = isRegion;
    }

    public AnnotationDefinition time(Long time) {

        this.time = time;
        return this;
    }

    /**
     * Get time
     *
     * @return time
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public AnnotationDefinition timeEnd(Long timeEnd) {

        this.timeEnd = timeEnd;
        return this;
    }

    /**
     * Get timeEnd
     *
     * @return timeEnd
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public AnnotationDefinition tags(List<String> tags) {

        this.tags = tags;
        return this;
    }

    public AnnotationDefinition addTagsItem(String tagsItem) {
        if (this.tags == null) {
            this.tags = new ArrayList<String>();
        }
        this.tags.add(tagsItem);
        return this;
    }

    /**
     * Get tags
     *
     * @return tags
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public AnnotationDefinition changeId(Integer changeId) {

        this.changeId = changeId;
        return this;
    }

    /**
     * Get changeId
     *
     * @return changeId
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getChangeId() {
        return changeId;
    }

    public void setChangeId(Integer changeId) {
        this.changeId = changeId;
    }

    public AnnotationDefinition variableId(Integer variableId) {

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

    public AnnotationDefinition runId(Integer runId) {

        this.runId = runId;
        return this;
    }

    /**
     * Get runId
     *
     * @return runId
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }

    public AnnotationDefinition datasetOrdinal(Integer datasetOrdinal) {

        this.datasetOrdinal = datasetOrdinal;
        return this;
    }

    /**
     * Get datasetOrdinal
     *
     * @return datasetOrdinal
     **/
    @javax.annotation.Nullable
    @ApiModelProperty(value = "")

    public Integer getDatasetOrdinal() {
        return datasetOrdinal;
    }

    public void setDatasetOrdinal(Integer datasetOrdinal) {
        this.datasetOrdinal = datasetOrdinal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnnotationDefinition annotationDefinition = (AnnotationDefinition) o;
        return Objects.equals(this.title, annotationDefinition.title) &&
            Objects.equals(this.text, annotationDefinition.text) &&
            Objects.equals(this.isRegion, annotationDefinition.isRegion) &&
            Objects.equals(this.time, annotationDefinition.time) &&
            Objects.equals(this.timeEnd, annotationDefinition.timeEnd) &&
            Objects.equals(this.tags, annotationDefinition.tags) &&
            Objects.equals(this.changeId, annotationDefinition.changeId) &&
            Objects.equals(this.variableId, annotationDefinition.variableId) &&
            Objects.equals(this.runId, annotationDefinition.runId) &&
            Objects.equals(this.datasetOrdinal, annotationDefinition.datasetOrdinal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, text, isRegion, time, timeEnd, tags, changeId, variableId, runId, datasetOrdinal);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AnnotationDefinition {\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    text: ").append(toIndentedString(text)).append("\n");
        sb.append("    isRegion: ").append(toIndentedString(isRegion)).append("\n");
        sb.append("    time: ").append(toIndentedString(time)).append("\n");
        sb.append("    timeEnd: ").append(toIndentedString(timeEnd)).append("\n");
        sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
        sb.append("    changeId: ").append(toIndentedString(changeId)).append("\n");
        sb.append("    variableId: ").append(toIndentedString(variableId)).append("\n");
        sb.append("    runId: ").append(toIndentedString(runId)).append("\n");
        sb.append("    datasetOrdinal: ").append(toIndentedString(datasetOrdinal)).append("\n");
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

