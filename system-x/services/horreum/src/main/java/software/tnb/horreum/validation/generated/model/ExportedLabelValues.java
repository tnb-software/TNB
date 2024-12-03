/*
 * Horreum REST API
 * Horreum automated change anomaly detection. For more information, please see [https://horreum.hyperfoil.io/](https://horreum.hyperfoil.io/)
 *
 * The version of the OpenAPI document: 0.17
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package software.tnb.horreum.validation.generated.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.OffsetDateTime;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import software.tnb.horreum.validation.generated.JSON;

/**
 * A map of label names to label values with the associated datasetId and runId
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]", comments = "Generator version: 7.10.0")
public class ExportedLabelValues {
  public static final String SERIALIZED_NAME_VALUES = "values";
  @SerializedName(SERIALIZED_NAME_VALUES)
  @javax.annotation.Nullable
  private Object values;

  public static final String SERIALIZED_NAME_RUN_ID = "runId";
  @SerializedName(SERIALIZED_NAME_RUN_ID)
  @javax.annotation.Nullable
  private Integer runId;

  public static final String SERIALIZED_NAME_DATASET_ID = "datasetId";
  @SerializedName(SERIALIZED_NAME_DATASET_ID)
  @javax.annotation.Nullable
  private Integer datasetId;

  public static final String SERIALIZED_NAME_START = "start";
  @SerializedName(SERIALIZED_NAME_START)
  @javax.annotation.Nonnull
  private OffsetDateTime start;

  public static final String SERIALIZED_NAME_STOP = "stop";
  @SerializedName(SERIALIZED_NAME_STOP)
  @javax.annotation.Nonnull
  private OffsetDateTime stop;

  public ExportedLabelValues() {
  }

  public ExportedLabelValues values(@javax.annotation.Nullable Object values) {
    this.values = values;
    return this;
  }

  /**
   * a map of label name to value
   * @return values
   */
  @javax.annotation.Nullable
  public Object getValues() {
    return values;
  }

  public void setValues(@javax.annotation.Nullable Object values) {
    this.values = values;
  }


  public ExportedLabelValues runId(@javax.annotation.Nullable Integer runId) {
    this.runId = runId;
    return this;
  }

  /**
   * the run id that created the dataset
   * @return runId
   */
  @javax.annotation.Nullable
  public Integer getRunId() {
    return runId;
  }

  public void setRunId(@javax.annotation.Nullable Integer runId) {
    this.runId = runId;
  }


  public ExportedLabelValues datasetId(@javax.annotation.Nullable Integer datasetId) {
    this.datasetId = datasetId;
    return this;
  }

  /**
   * the unique dataset id
   * @return datasetId
   */
  @javax.annotation.Nullable
  public Integer getDatasetId() {
    return datasetId;
  }

  public void setDatasetId(@javax.annotation.Nullable Integer datasetId) {
    this.datasetId = datasetId;
  }


  public ExportedLabelValues start(@javax.annotation.Nonnull OffsetDateTime start) {
    this.start = start;
    return this;
  }

  /**
   * Start timestamp
   * @return start
   */
  @javax.annotation.Nonnull
  public OffsetDateTime getStart() {
    return start;
  }

  public void setStart(@javax.annotation.Nonnull OffsetDateTime start) {
    this.start = start;
  }


  public ExportedLabelValues stop(@javax.annotation.Nonnull OffsetDateTime stop) {
    this.stop = stop;
    return this;
  }

  /**
   * Stop timestamp
   * @return stop
   */
  @javax.annotation.Nonnull
  public OffsetDateTime getStop() {
    return stop;
  }

  public void setStop(@javax.annotation.Nonnull OffsetDateTime stop) {
    this.stop = stop;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExportedLabelValues exportedLabelValues = (ExportedLabelValues) o;
    return Objects.equals(this.values, exportedLabelValues.values) &&
        Objects.equals(this.runId, exportedLabelValues.runId) &&
        Objects.equals(this.datasetId, exportedLabelValues.datasetId) &&
        Objects.equals(this.start, exportedLabelValues.start) &&
        Objects.equals(this.stop, exportedLabelValues.stop);
  }

  @Override
  public int hashCode() {
    return Objects.hash(values, runId, datasetId, start, stop);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExportedLabelValues {\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
    sb.append("    runId: ").append(toIndentedString(runId)).append("\n");
    sb.append("    datasetId: ").append(toIndentedString(datasetId)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    stop: ").append(toIndentedString(stop)).append("\n");
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


  public static HashSet<String> openapiFields;
  public static HashSet<String> openapiRequiredFields;

  static {
    // a set of all properties/fields (JSON key names)
    openapiFields = new HashSet<String>();
    openapiFields.add("values");
    openapiFields.add("runId");
    openapiFields.add("datasetId");
    openapiFields.add("start");
    openapiFields.add("stop");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("start");
    openapiRequiredFields.add("stop");
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to ExportedLabelValues
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ExportedLabelValues.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ExportedLabelValues is not found in the empty JSON string", ExportedLabelValues.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!ExportedLabelValues.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `ExportedLabelValues` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : ExportedLabelValues.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ExportedLabelValues.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ExportedLabelValues' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ExportedLabelValues> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ExportedLabelValues.class));

       return (TypeAdapter<T>) new TypeAdapter<ExportedLabelValues>() {
           @Override
           public void write(JsonWriter out, ExportedLabelValues value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public ExportedLabelValues read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of ExportedLabelValues given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of ExportedLabelValues
   * @throws IOException if the JSON string is invalid with respect to ExportedLabelValues
   */
  public static ExportedLabelValues fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ExportedLabelValues.class);
  }

  /**
   * Convert an instance of ExportedLabelValues to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

