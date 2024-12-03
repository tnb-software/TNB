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
 * A dataset is the JSON document used as the basis for all comparisons and reporting
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]", comments = "Generator version: 7.10.0")
public class Dataset {
  public static final String SERIALIZED_NAME_ID = "id";
  @SerializedName(SERIALIZED_NAME_ID)
  @javax.annotation.Nullable
  private Integer id;

  public static final String SERIALIZED_NAME_DESCRIPTION = "description";
  @SerializedName(SERIALIZED_NAME_DESCRIPTION)
  @javax.annotation.Nullable
  private String description;

  public static final String SERIALIZED_NAME_TESTID = "testid";
  @SerializedName(SERIALIZED_NAME_TESTID)
  @javax.annotation.Nonnull
  private Integer testid;

  public static final String SERIALIZED_NAME_DATA = "data";
  @SerializedName(SERIALIZED_NAME_DATA)
  @javax.annotation.Nonnull
  private String data;

  public static final String SERIALIZED_NAME_ORDINAL = "ordinal";
  @SerializedName(SERIALIZED_NAME_ORDINAL)
  @javax.annotation.Nonnull
  private Integer ordinal;

  public static final String SERIALIZED_NAME_VALIDATION_ERRORS = "validationErrors";
  @SerializedName(SERIALIZED_NAME_VALIDATION_ERRORS)
  @javax.annotation.Nullable
  private List<ValidationError> validationErrors = new ArrayList<>();

  public static final String SERIALIZED_NAME_RUN_ID = "runId";
  @SerializedName(SERIALIZED_NAME_RUN_ID)
  @javax.annotation.Nullable
  private Integer runId;

  public static final String SERIALIZED_NAME_ACCESS = "access";
  @SerializedName(SERIALIZED_NAME_ACCESS)
  @javax.annotation.Nonnull
  private Access access;

  public static final String SERIALIZED_NAME_OWNER = "owner";
  @SerializedName(SERIALIZED_NAME_OWNER)
  @javax.annotation.Nonnull
  private String owner;

  public static final String SERIALIZED_NAME_START = "start";
  @SerializedName(SERIALIZED_NAME_START)
  @javax.annotation.Nonnull
  private Long start;

  public static final String SERIALIZED_NAME_STOP = "stop";
  @SerializedName(SERIALIZED_NAME_STOP)
  @javax.annotation.Nonnull
  private Long stop;

  public Dataset() {
  }

  public Dataset id(@javax.annotation.Nullable Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Dataset Unique ID
   * @return id
   */
  @javax.annotation.Nullable
  public Integer getId() {
    return id;
  }

  public void setId(@javax.annotation.Nullable Integer id) {
    this.id = id;
  }


  public Dataset description(@javax.annotation.Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Run description
   * @return description
   */
  @javax.annotation.Nullable
  public String getDescription() {
    return description;
  }

  public void setDescription(@javax.annotation.Nullable String description) {
    this.description = description;
  }


  public Dataset testid(@javax.annotation.Nonnull Integer testid) {
    this.testid = testid;
    return this;
  }

  /**
   * Test ID that Dataset relates to
   * @return testid
   */
  @javax.annotation.Nonnull
  public Integer getTestid() {
    return testid;
  }

  public void setTestid(@javax.annotation.Nonnull Integer testid) {
    this.testid = testid;
  }


  public Dataset data(@javax.annotation.Nonnull String data) {
    this.data = data;
    return this;
  }

  /**
   * Data payload
   * @return data
   */
  @javax.annotation.Nonnull
  public String getData() {
    return data;
  }

  public void setData(@javax.annotation.Nonnull String data) {
    this.data = data;
  }


  public Dataset ordinal(@javax.annotation.Nonnull Integer ordinal) {
    this.ordinal = ordinal;
    return this;
  }

  /**
   * Dataset ordinal for ordered list of Datasets derived from a Run
   * @return ordinal
   */
  @javax.annotation.Nonnull
  public Integer getOrdinal() {
    return ordinal;
  }

  public void setOrdinal(@javax.annotation.Nonnull Integer ordinal) {
    this.ordinal = ordinal;
  }


  public Dataset validationErrors(@javax.annotation.Nullable List<ValidationError> validationErrors) {
    this.validationErrors = validationErrors;
    return this;
  }

  public Dataset addValidationErrorsItem(ValidationError validationErrorsItem) {
    if (this.validationErrors == null) {
      this.validationErrors = new ArrayList<>();
    }
    this.validationErrors.add(validationErrorsItem);
    return this;
  }

  /**
   * List of Validation Errors
   * @return validationErrors
   */
  @javax.annotation.Nullable
  public List<ValidationError> getValidationErrors() {
    return validationErrors;
  }

  public void setValidationErrors(@javax.annotation.Nullable List<ValidationError> validationErrors) {
    this.validationErrors = validationErrors;
  }


  public Dataset runId(@javax.annotation.Nullable Integer runId) {
    this.runId = runId;
    return this;
  }

  /**
   * Run ID that Dataset relates to
   * @return runId
   */
  @javax.annotation.Nullable
  public Integer getRunId() {
    return runId;
  }

  public void setRunId(@javax.annotation.Nullable Integer runId) {
    this.runId = runId;
  }


  public Dataset access(@javax.annotation.Nonnull Access access) {
    this.access = access;
    return this;
  }

  /**
   * Access rights for the test. This defines the visibility of the Test in the UI
   * @return access
   */
  @javax.annotation.Nonnull
  public Access getAccess() {
    return access;
  }

  public void setAccess(@javax.annotation.Nonnull Access access) {
    this.access = access;
  }


  public Dataset owner(@javax.annotation.Nonnull String owner) {
    this.owner = owner;
    return this;
  }

  /**
   * Name of the team that owns the test. Users must belong to the team that owns a test to make modifications
   * @return owner
   */
  @javax.annotation.Nonnull
  public String getOwner() {
    return owner;
  }

  public void setOwner(@javax.annotation.Nonnull String owner) {
    this.owner = owner;
  }


  public Dataset start(@javax.annotation.Nonnull Long start) {
    this.start = start;
    return this;
  }

  /**
   * Run Start timestamp
   * @return start
   */
  @javax.annotation.Nonnull
  public Long getStart() {
    return start;
  }

  public void setStart(@javax.annotation.Nonnull Long start) {
    this.start = start;
  }


  public Dataset stop(@javax.annotation.Nonnull Long stop) {
    this.stop = stop;
    return this;
  }

  /**
   * Run Stop timestamp
   * @return stop
   */
  @javax.annotation.Nonnull
  public Long getStop() {
    return stop;
  }

  public void setStop(@javax.annotation.Nonnull Long stop) {
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
    Dataset dataset = (Dataset) o;
    return Objects.equals(this.id, dataset.id) &&
        Objects.equals(this.description, dataset.description) &&
        Objects.equals(this.testid, dataset.testid) &&
        Objects.equals(this.data, dataset.data) &&
        Objects.equals(this.ordinal, dataset.ordinal) &&
        Objects.equals(this.validationErrors, dataset.validationErrors) &&
        Objects.equals(this.runId, dataset.runId) &&
        Objects.equals(this.access, dataset.access) &&
        Objects.equals(this.owner, dataset.owner) &&
        Objects.equals(this.start, dataset.start) &&
        Objects.equals(this.stop, dataset.stop);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, testid, data, ordinal, validationErrors, runId, access, owner, start, stop);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Dataset {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    testid: ").append(toIndentedString(testid)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    ordinal: ").append(toIndentedString(ordinal)).append("\n");
    sb.append("    validationErrors: ").append(toIndentedString(validationErrors)).append("\n");
    sb.append("    runId: ").append(toIndentedString(runId)).append("\n");
    sb.append("    access: ").append(toIndentedString(access)).append("\n");
    sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
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
    openapiFields.add("access");
    openapiFields.add("owner");
    openapiFields.add("start");
    openapiFields.add("stop");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("testid");
    openapiRequiredFields.add("data");
    openapiRequiredFields.add("ordinal");
    openapiRequiredFields.add("access");
    openapiRequiredFields.add("owner");
    openapiRequiredFields.add("start");
    openapiRequiredFields.add("stop");
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to Dataset
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!Dataset.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in Dataset is not found in the empty JSON string", Dataset.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!Dataset.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `Dataset` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : Dataset.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("description") != null && !jsonObj.get("description").isJsonNull()) && !jsonObj.get("description").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `description` to be a primitive type in the JSON string but got `%s`", jsonObj.get("description").toString()));
      }
      if (!jsonObj.get("data").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `data` to be a primitive type in the JSON string but got `%s`", jsonObj.get("data").toString()));
      }
      if (jsonObj.get("validationErrors") != null && !jsonObj.get("validationErrors").isJsonNull()) {
        JsonArray jsonArrayvalidationErrors = jsonObj.getAsJsonArray("validationErrors");
        if (jsonArrayvalidationErrors != null) {
          // ensure the json data is an array
          if (!jsonObj.get("validationErrors").isJsonArray()) {
            throw new IllegalArgumentException(String.format("Expected the field `validationErrors` to be an array in the JSON string but got `%s`", jsonObj.get("validationErrors").toString()));
          }

          // validate the optional field `validationErrors` (array)
          for (int i = 0; i < jsonArrayvalidationErrors.size(); i++) {
            ValidationError.validateJsonElement(jsonArrayvalidationErrors.get(i));
          };
        }
      }
      // validate the required field `access`
      Access.validateJsonElement(jsonObj.get("access"));
      if (!jsonObj.get("owner").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `owner` to be a primitive type in the JSON string but got `%s`", jsonObj.get("owner").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!Dataset.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'Dataset' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<Dataset> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(Dataset.class));

       return (TypeAdapter<T>) new TypeAdapter<Dataset>() {
           @Override
           public void write(JsonWriter out, Dataset value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public Dataset read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of Dataset given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of Dataset
   * @throws IOException if the JSON string is invalid with respect to Dataset
   */
  public static Dataset fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, Dataset.class);
  }

  /**
   * Convert an instance of Dataset to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

