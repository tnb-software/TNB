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
 * Action Log
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]", comments = "Generator version: 7.10.0")
public class ActionLog {
  public static final String SERIALIZED_NAME_TEST_ID = "testId";
  @SerializedName(SERIALIZED_NAME_TEST_ID)
  @javax.annotation.Nonnull
  private Integer testId;

  public static final String SERIALIZED_NAME_EVENT = "event";
  @SerializedName(SERIALIZED_NAME_EVENT)
  @javax.annotation.Nonnull
  private String event;

  public static final String SERIALIZED_NAME_TYPE = "type";
  @SerializedName(SERIALIZED_NAME_TYPE)
  @javax.annotation.Nullable
  private String type;

  public static final String SERIALIZED_NAME_ID = "id";
  @SerializedName(SERIALIZED_NAME_ID)
  @javax.annotation.Nonnull
  private Long id;

  public static final String SERIALIZED_NAME_LEVEL = "level";
  @SerializedName(SERIALIZED_NAME_LEVEL)
  @javax.annotation.Nonnull
  private Integer level;

  public static final String SERIALIZED_NAME_TIMESTAMP = "timestamp";
  @SerializedName(SERIALIZED_NAME_TIMESTAMP)
  @javax.annotation.Nonnull
  private OffsetDateTime timestamp;

  public static final String SERIALIZED_NAME_MESSAGE = "message";
  @SerializedName(SERIALIZED_NAME_MESSAGE)
  @javax.annotation.Nonnull
  private String message;

  public ActionLog() {
  }

  public ActionLog testId(@javax.annotation.Nonnull Integer testId) {
    this.testId = testId;
    return this;
  }

  /**
   * Get testId
   * @return testId
   */
  @javax.annotation.Nonnull
  public Integer getTestId() {
    return testId;
  }

  public void setTestId(@javax.annotation.Nonnull Integer testId) {
    this.testId = testId;
  }


  public ActionLog event(@javax.annotation.Nonnull String event) {
    this.event = event;
    return this;
  }

  /**
   * Get event
   * @return event
   */
  @javax.annotation.Nonnull
  public String getEvent() {
    return event;
  }

  public void setEvent(@javax.annotation.Nonnull String event) {
    this.event = event;
  }


  public ActionLog type(@javax.annotation.Nullable String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  @javax.annotation.Nullable
  public String getType() {
    return type;
  }

  public void setType(@javax.annotation.Nullable String type) {
    this.type = type;
  }


  public ActionLog id(@javax.annotation.Nonnull Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @javax.annotation.Nonnull
  public Long getId() {
    return id;
  }

  public void setId(@javax.annotation.Nonnull Long id) {
    this.id = id;
  }


  public ActionLog level(@javax.annotation.Nonnull Integer level) {
    this.level = level;
    return this;
  }

  /**
   * Get level
   * @return level
   */
  @javax.annotation.Nonnull
  public Integer getLevel() {
    return level;
  }

  public void setLevel(@javax.annotation.Nonnull Integer level) {
    this.level = level;
  }


  public ActionLog timestamp(@javax.annotation.Nonnull OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Get timestamp
   * @return timestamp
   */
  @javax.annotation.Nonnull
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(@javax.annotation.Nonnull OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }


  public ActionLog message(@javax.annotation.Nonnull String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  @javax.annotation.Nonnull
  public String getMessage() {
    return message;
  }

  public void setMessage(@javax.annotation.Nonnull String message) {
    this.message = message;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ActionLog actionLog = (ActionLog) o;
    return Objects.equals(this.testId, actionLog.testId) &&
        Objects.equals(this.event, actionLog.event) &&
        Objects.equals(this.type, actionLog.type) &&
        Objects.equals(this.id, actionLog.id) &&
        Objects.equals(this.level, actionLog.level) &&
        Objects.equals(this.timestamp, actionLog.timestamp) &&
        Objects.equals(this.message, actionLog.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(testId, event, type, id, level, timestamp, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ActionLog {\n");
    sb.append("    testId: ").append(toIndentedString(testId)).append("\n");
    sb.append("    event: ").append(toIndentedString(event)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    level: ").append(toIndentedString(level)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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
    openapiFields.add("level");
    openapiFields.add("timestamp");
    openapiFields.add("message");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("testId");
    openapiRequiredFields.add("event");
    openapiRequiredFields.add("id");
    openapiRequiredFields.add("level");
    openapiRequiredFields.add("timestamp");
    openapiRequiredFields.add("message");
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to ActionLog
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ActionLog.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ActionLog is not found in the empty JSON string", ActionLog.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!ActionLog.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `ActionLog` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : ActionLog.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if (!jsonObj.get("event").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `event` to be a primitive type in the JSON string but got `%s`", jsonObj.get("event").toString()));
      }
      if ((jsonObj.get("type") != null && !jsonObj.get("type").isJsonNull()) && !jsonObj.get("type").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `type` to be a primitive type in the JSON string but got `%s`", jsonObj.get("type").toString()));
      }
      if (!jsonObj.get("message").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `message` to be a primitive type in the JSON string but got `%s`", jsonObj.get("message").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ActionLog.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ActionLog' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ActionLog> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ActionLog.class));

       return (TypeAdapter<T>) new TypeAdapter<ActionLog>() {
           @Override
           public void write(JsonWriter out, ActionLog value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public ActionLog read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of ActionLog given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of ActionLog
   * @throws IOException if the JSON string is invalid with respect to ActionLog
   */
  public static ActionLog fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ActionLog.class);
  }

  /**
   * Convert an instance of ActionLog to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

