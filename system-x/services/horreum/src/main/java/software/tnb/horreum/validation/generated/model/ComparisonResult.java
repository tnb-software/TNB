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
 * Result of performing a Comparison
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]", comments = "Generator version: 7.10.0")
public class ComparisonResult {
  public static final String SERIALIZED_NAME_OVERALL = "overall";
  @SerializedName(SERIALIZED_NAME_OVERALL)
  @javax.annotation.Nullable
  private BetterOrWorse overall;

  public static final String SERIALIZED_NAME_EXPERIMENT_VALUE = "experimentValue";
  @SerializedName(SERIALIZED_NAME_EXPERIMENT_VALUE)
  @javax.annotation.Nullable
  private Double experimentValue;

  public static final String SERIALIZED_NAME_BASELINE_VALUE = "baselineValue";
  @SerializedName(SERIALIZED_NAME_BASELINE_VALUE)
  @javax.annotation.Nullable
  private Double baselineValue;

  public static final String SERIALIZED_NAME_RESULT = "result";
  @SerializedName(SERIALIZED_NAME_RESULT)
  @javax.annotation.Nullable
  private String result;

  public ComparisonResult() {
  }

  public ComparisonResult overall(@javax.annotation.Nullable BetterOrWorse overall) {
    this.overall = overall;
    return this;
  }

  /**
   * Was the Experiment dataset better or worse than the baseline dataset
   * @return overall
   */
  @javax.annotation.Nullable
  public BetterOrWorse getOverall() {
    return overall;
  }

  public void setOverall(@javax.annotation.Nullable BetterOrWorse overall) {
    this.overall = overall;
  }


  public ComparisonResult experimentValue(@javax.annotation.Nullable Double experimentValue) {
    this.experimentValue = experimentValue;
    return this;
  }

  /**
   * Experiment value
   * @return experimentValue
   */
  @javax.annotation.Nullable
  public Double getExperimentValue() {
    return experimentValue;
  }

  public void setExperimentValue(@javax.annotation.Nullable Double experimentValue) {
    this.experimentValue = experimentValue;
  }


  public ComparisonResult baselineValue(@javax.annotation.Nullable Double baselineValue) {
    this.baselineValue = baselineValue;
    return this;
  }

  /**
   * Baseline value
   * @return baselineValue
   */
  @javax.annotation.Nullable
  public Double getBaselineValue() {
    return baselineValue;
  }

  public void setBaselineValue(@javax.annotation.Nullable Double baselineValue) {
    this.baselineValue = baselineValue;
  }


  public ComparisonResult result(@javax.annotation.Nullable String result) {
    this.result = result;
    return this;
  }

  /**
   * The relative difference between the Experiment and Baseline Datasets
   * @return result
   */
  @javax.annotation.Nullable
  public String getResult() {
    return result;
  }

  public void setResult(@javax.annotation.Nullable String result) {
    this.result = result;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComparisonResult comparisonResult = (ComparisonResult) o;
    return Objects.equals(this.overall, comparisonResult.overall) &&
        Objects.equals(this.experimentValue, comparisonResult.experimentValue) &&
        Objects.equals(this.baselineValue, comparisonResult.baselineValue) &&
        Objects.equals(this.result, comparisonResult.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(overall, experimentValue, baselineValue, result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ComparisonResult {\n");
    sb.append("    overall: ").append(toIndentedString(overall)).append("\n");
    sb.append("    experimentValue: ").append(toIndentedString(experimentValue)).append("\n");
    sb.append("    baselineValue: ").append(toIndentedString(baselineValue)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
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
    openapiFields.add("overall");
    openapiFields.add("experimentValue");
    openapiFields.add("baselineValue");
    openapiFields.add("result");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to ComparisonResult
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ComparisonResult.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ComparisonResult is not found in the empty JSON string", ComparisonResult.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!ComparisonResult.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `ComparisonResult` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      // validate the optional field `overall`
      if (jsonObj.get("overall") != null && !jsonObj.get("overall").isJsonNull()) {
        BetterOrWorse.validateJsonElement(jsonObj.get("overall"));
      }
      if ((jsonObj.get("result") != null && !jsonObj.get("result").isJsonNull()) && !jsonObj.get("result").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `result` to be a primitive type in the JSON string but got `%s`", jsonObj.get("result").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ComparisonResult.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ComparisonResult' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ComparisonResult> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ComparisonResult.class));

       return (TypeAdapter<T>) new TypeAdapter<ComparisonResult>() {
           @Override
           public void write(JsonWriter out, ComparisonResult value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public ComparisonResult read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of ComparisonResult given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of ComparisonResult
   * @throws IOException if the JSON string is invalid with respect to ComparisonResult
   */
  public static ComparisonResult fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ComparisonResult.class);
  }

  /**
   * Convert an instance of ComparisonResult to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

