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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import software.tnb.horreum.validation.generated.JSON;

/**
 * Table Report
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]", comments = "Generator version: 7.10.0")
public class TableReport {
  public static final String SERIALIZED_NAME_ID = "id";
  @SerializedName(SERIALIZED_NAME_ID)
  @javax.annotation.Nonnull
  private Integer id;

  public static final String SERIALIZED_NAME_CONFIG = "config";
  @SerializedName(SERIALIZED_NAME_CONFIG)
  @javax.annotation.Nonnull
  private TableReportConfig config;

  public static final String SERIALIZED_NAME_CREATED = "created";
  @SerializedName(SERIALIZED_NAME_CREATED)
  @javax.annotation.Nonnull
  private OffsetDateTime created;

  public static final String SERIALIZED_NAME_COMMENTS = "comments";
  @SerializedName(SERIALIZED_NAME_COMMENTS)
  @javax.annotation.Nonnull
  private List<TableReportCommentsInner> comments = new ArrayList<>();

  public static final String SERIALIZED_NAME_DATA = "data";
  @SerializedName(SERIALIZED_NAME_DATA)
  @javax.annotation.Nonnull
  private List<TableReportDataInner> data = new ArrayList<>();

  public static final String SERIALIZED_NAME_LOGS = "logs";
  @SerializedName(SERIALIZED_NAME_LOGS)
  @javax.annotation.Nonnull
  private List<TableReportLogsInner> logs = new ArrayList<>();

  public TableReport() {
  }

  public TableReport id(@javax.annotation.Nonnull Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @javax.annotation.Nonnull
  public Integer getId() {
    return id;
  }

  public void setId(@javax.annotation.Nonnull Integer id) {
    this.id = id;
  }


  public TableReport config(@javax.annotation.Nonnull TableReportConfig config) {
    this.config = config;
    return this;
  }

  /**
   * Get config
   * @return config
   */
  @javax.annotation.Nonnull
  public TableReportConfig getConfig() {
    return config;
  }

  public void setConfig(@javax.annotation.Nonnull TableReportConfig config) {
    this.config = config;
  }


  public TableReport created(@javax.annotation.Nonnull OffsetDateTime created) {
    this.created = created;
    return this;
  }

  /**
   * Created timestamp
   * @return created
   */
  @javax.annotation.Nonnull
  public OffsetDateTime getCreated() {
    return created;
  }

  public void setCreated(@javax.annotation.Nonnull OffsetDateTime created) {
    this.created = created;
  }


  public TableReport comments(@javax.annotation.Nonnull List<TableReportCommentsInner> comments) {
    this.comments = comments;
    return this;
  }

  public TableReport addCommentsItem(TableReportCommentsInner commentsItem) {
    if (this.comments == null) {
      this.comments = new ArrayList<>();
    }
    this.comments.add(commentsItem);
    return this;
  }

  /**
   * List of ReportComments
   * @return comments
   */
  @javax.annotation.Nonnull
  public List<TableReportCommentsInner> getComments() {
    return comments;
  }

  public void setComments(@javax.annotation.Nonnull List<TableReportCommentsInner> comments) {
    this.comments = comments;
  }


  public TableReport data(@javax.annotation.Nonnull List<TableReportDataInner> data) {
    this.data = data;
    return this;
  }

  public TableReport addDataItem(TableReportDataInner dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<>();
    }
    this.data.add(dataItem);
    return this;
  }

  /**
   * List of TableReportData
   * @return data
   */
  @javax.annotation.Nonnull
  public List<TableReportDataInner> getData() {
    return data;
  }

  public void setData(@javax.annotation.Nonnull List<TableReportDataInner> data) {
    this.data = data;
  }


  public TableReport logs(@javax.annotation.Nonnull List<TableReportLogsInner> logs) {
    this.logs = logs;
    return this;
  }

  public TableReport addLogsItem(TableReportLogsInner logsItem) {
    if (this.logs == null) {
      this.logs = new ArrayList<>();
    }
    this.logs.add(logsItem);
    return this;
  }

  /**
   * List of ReportLogs
   * @return logs
   */
  @javax.annotation.Nonnull
  public List<TableReportLogsInner> getLogs() {
    return logs;
  }

  public void setLogs(@javax.annotation.Nonnull List<TableReportLogsInner> logs) {
    this.logs = logs;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TableReport tableReport = (TableReport) o;
    return Objects.equals(this.id, tableReport.id) &&
        Objects.equals(this.config, tableReport.config) &&
        Objects.equals(this.created, tableReport.created) &&
        Objects.equals(this.comments, tableReport.comments) &&
        Objects.equals(this.data, tableReport.data) &&
        Objects.equals(this.logs, tableReport.logs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, config, created, comments, data, logs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TableReport {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    config: ").append(toIndentedString(config)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    comments: ").append(toIndentedString(comments)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    logs: ").append(toIndentedString(logs)).append("\n");
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
    openapiFields.add("id");
    openapiFields.add("config");
    openapiFields.add("created");
    openapiFields.add("comments");
    openapiFields.add("data");
    openapiFields.add("logs");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("id");
    openapiRequiredFields.add("config");
    openapiRequiredFields.add("created");
    openapiRequiredFields.add("comments");
    openapiRequiredFields.add("data");
    openapiRequiredFields.add("logs");
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to TableReport
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!TableReport.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in TableReport is not found in the empty JSON string", TableReport.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!TableReport.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `TableReport` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : TableReport.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      // validate the required field `config`
      TableReportConfig.validateJsonElement(jsonObj.get("config"));
      // ensure the json data is an array
      if (!jsonObj.get("comments").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `comments` to be an array in the JSON string but got `%s`", jsonObj.get("comments").toString()));
      }

      JsonArray jsonArraycomments = jsonObj.getAsJsonArray("comments");
      // validate the required field `comments` (array)
      for (int i = 0; i < jsonArraycomments.size(); i++) {
        TableReportCommentsInner.validateJsonElement(jsonArraycomments.get(i));
      };
      // ensure the json data is an array
      if (!jsonObj.get("data").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `data` to be an array in the JSON string but got `%s`", jsonObj.get("data").toString()));
      }

      JsonArray jsonArraydata = jsonObj.getAsJsonArray("data");
      // validate the required field `data` (array)
      for (int i = 0; i < jsonArraydata.size(); i++) {
        TableReportDataInner.validateJsonElement(jsonArraydata.get(i));
      };
      // ensure the json data is an array
      if (!jsonObj.get("logs").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `logs` to be an array in the JSON string but got `%s`", jsonObj.get("logs").toString()));
      }

      JsonArray jsonArraylogs = jsonObj.getAsJsonArray("logs");
      // validate the required field `logs` (array)
      for (int i = 0; i < jsonArraylogs.size(); i++) {
        TableReportLogsInner.validateJsonElement(jsonArraylogs.get(i));
      };
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!TableReport.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'TableReport' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<TableReport> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(TableReport.class));

       return (TypeAdapter<T>) new TypeAdapter<TableReport>() {
           @Override
           public void write(JsonWriter out, TableReport value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public TableReport read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of TableReport given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of TableReport
   * @throws IOException if the JSON string is invalid with respect to TableReport
   */
  public static TableReport fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, TableReport.class);
  }

  /**
   * Convert an instance of TableReport to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

