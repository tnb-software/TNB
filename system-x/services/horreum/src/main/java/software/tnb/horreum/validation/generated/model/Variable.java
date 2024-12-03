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
 * Variable
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]", comments = "Generator version: 7.10.0")
public class Variable {
  public static final String SERIALIZED_NAME_ID = "id";
  @SerializedName(SERIALIZED_NAME_ID)
  @javax.annotation.Nonnull
  private Integer id;

  public static final String SERIALIZED_NAME_TEST_ID = "testId";
  @SerializedName(SERIALIZED_NAME_TEST_ID)
  @javax.annotation.Nonnull
  private Integer testId;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  @javax.annotation.Nonnull
  private String name;

  public static final String SERIALIZED_NAME_GROUP = "group";
  @SerializedName(SERIALIZED_NAME_GROUP)
  @javax.annotation.Nullable
  private String group;

  public static final String SERIALIZED_NAME_ORDER = "order";
  @SerializedName(SERIALIZED_NAME_ORDER)
  @javax.annotation.Nonnull
  private Integer order;

  public static final String SERIALIZED_NAME_LABELS = "labels";
  @SerializedName(SERIALIZED_NAME_LABELS)
  @javax.annotation.Nonnull
  private List<String> labels = new ArrayList<>();

  public static final String SERIALIZED_NAME_CALCULATION = "calculation";
  @SerializedName(SERIALIZED_NAME_CALCULATION)
  @javax.annotation.Nullable
  private String calculation;

  public static final String SERIALIZED_NAME_CHANGE_DETECTION = "changeDetection";
  @SerializedName(SERIALIZED_NAME_CHANGE_DETECTION)
  @javax.annotation.Nonnull
  private List<ChangeDetection> changeDetection = new ArrayList<>();

  public Variable() {
  }

  public Variable id(@javax.annotation.Nonnull Integer id) {
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


  public Variable testId(@javax.annotation.Nonnull Integer testId) {
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


  public Variable name(@javax.annotation.Nonnull String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @javax.annotation.Nonnull
  public String getName() {
    return name;
  }

  public void setName(@javax.annotation.Nonnull String name) {
    this.name = name;
  }


  public Variable group(@javax.annotation.Nullable String group) {
    this.group = group;
    return this;
  }

  /**
   * Get group
   * @return group
   */
  @javax.annotation.Nullable
  public String getGroup() {
    return group;
  }

  public void setGroup(@javax.annotation.Nullable String group) {
    this.group = group;
  }


  public Variable order(@javax.annotation.Nonnull Integer order) {
    this.order = order;
    return this;
  }

  /**
   * Get order
   * @return order
   */
  @javax.annotation.Nonnull
  public Integer getOrder() {
    return order;
  }

  public void setOrder(@javax.annotation.Nonnull Integer order) {
    this.order = order;
  }


  public Variable labels(@javax.annotation.Nonnull List<String> labels) {
    this.labels = labels;
    return this;
  }

  public Variable addLabelsItem(String labelsItem) {
    if (this.labels == null) {
      this.labels = new ArrayList<>();
    }
    this.labels.add(labelsItem);
    return this;
  }

  /**
   * Get labels
   * @return labels
   */
  @javax.annotation.Nonnull
  public List<String> getLabels() {
    return labels;
  }

  public void setLabels(@javax.annotation.Nonnull List<String> labels) {
    this.labels = labels;
  }


  public Variable calculation(@javax.annotation.Nullable String calculation) {
    this.calculation = calculation;
    return this;
  }

  /**
   * Get calculation
   * @return calculation
   */
  @javax.annotation.Nullable
  public String getCalculation() {
    return calculation;
  }

  public void setCalculation(@javax.annotation.Nullable String calculation) {
    this.calculation = calculation;
  }


  public Variable changeDetection(@javax.annotation.Nonnull List<ChangeDetection> changeDetection) {
    this.changeDetection = changeDetection;
    return this;
  }

  public Variable addChangeDetectionItem(ChangeDetection changeDetectionItem) {
    if (this.changeDetection == null) {
      this.changeDetection = new ArrayList<>();
    }
    this.changeDetection.add(changeDetectionItem);
    return this;
  }

  /**
   * Get changeDetection
   * @return changeDetection
   */
  @javax.annotation.Nonnull
  public List<ChangeDetection> getChangeDetection() {
    return changeDetection;
  }

  public void setChangeDetection(@javax.annotation.Nonnull List<ChangeDetection> changeDetection) {
    this.changeDetection = changeDetection;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Variable variable = (Variable) o;
    return Objects.equals(this.id, variable.id) &&
        Objects.equals(this.testId, variable.testId) &&
        Objects.equals(this.name, variable.name) &&
        Objects.equals(this.group, variable.group) &&
        Objects.equals(this.order, variable.order) &&
        Objects.equals(this.labels, variable.labels) &&
        Objects.equals(this.calculation, variable.calculation) &&
        Objects.equals(this.changeDetection, variable.changeDetection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, testId, name, group, order, labels, calculation, changeDetection);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Variable {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    testId: ").append(toIndentedString(testId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    group: ").append(toIndentedString(group)).append("\n");
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
    sb.append("    labels: ").append(toIndentedString(labels)).append("\n");
    sb.append("    calculation: ").append(toIndentedString(calculation)).append("\n");
    sb.append("    changeDetection: ").append(toIndentedString(changeDetection)).append("\n");
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
    openapiFields.add("testId");
    openapiFields.add("name");
    openapiFields.add("group");
    openapiFields.add("order");
    openapiFields.add("labels");
    openapiFields.add("calculation");
    openapiFields.add("changeDetection");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("id");
    openapiRequiredFields.add("testId");
    openapiRequiredFields.add("name");
    openapiRequiredFields.add("order");
    openapiRequiredFields.add("labels");
    openapiRequiredFields.add("changeDetection");
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to Variable
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!Variable.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in Variable is not found in the empty JSON string", Variable.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!Variable.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `Variable` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : Variable.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if (!jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("group") != null && !jsonObj.get("group").isJsonNull()) && !jsonObj.get("group").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `group` to be a primitive type in the JSON string but got `%s`", jsonObj.get("group").toString()));
      }
      // ensure the required json array is present
      if (jsonObj.get("labels") == null) {
        throw new IllegalArgumentException("Expected the field `linkedContent` to be an array in the JSON string but got `null`");
      } else if (!jsonObj.get("labels").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `labels` to be an array in the JSON string but got `%s`", jsonObj.get("labels").toString()));
      }
      if ((jsonObj.get("calculation") != null && !jsonObj.get("calculation").isJsonNull()) && !jsonObj.get("calculation").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `calculation` to be a primitive type in the JSON string but got `%s`", jsonObj.get("calculation").toString()));
      }
      // ensure the json data is an array
      if (!jsonObj.get("changeDetection").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `changeDetection` to be an array in the JSON string but got `%s`", jsonObj.get("changeDetection").toString()));
      }

      JsonArray jsonArraychangeDetection = jsonObj.getAsJsonArray("changeDetection");
      // validate the required field `changeDetection` (array)
      for (int i = 0; i < jsonArraychangeDetection.size(); i++) {
        ChangeDetection.validateJsonElement(jsonArraychangeDetection.get(i));
      };
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!Variable.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'Variable' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<Variable> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(Variable.class));

       return (TypeAdapter<T>) new TypeAdapter<Variable>() {
           @Override
           public void write(JsonWriter out, Variable value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public Variable read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of Variable given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of Variable
   * @throws IOException if the JSON string is invalid with respect to Variable
   */
  public static Variable fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, Variable.class);
  }

  /**
   * Convert an instance of Variable to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

