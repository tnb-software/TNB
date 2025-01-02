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

import software.tnb.horreum.validation.generated.JSON;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Generated;

/**
 * EDivisiveDetectionConfig
 */
@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]",
    comments = "Generator version: 7.10.0")
public class EDivisiveDetectionConfig {
    public static final String SERIALIZED_NAME_BUILT_IN = "builtIn";
    @SerializedName(SERIALIZED_NAME_BUILT_IN)
    @javax.annotation.Nonnull
    private Boolean builtIn;

    /**
     * Gets or Sets model
     */
    @JsonAdapter(ModelEnum.Adapter.class)
    public enum ModelEnum {
        E_DIVISIVE("eDivisive");

        private String value;

        ModelEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static ModelEnum fromValue(String value) {
            for (ModelEnum b : ModelEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        public static class Adapter extends TypeAdapter<ModelEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final ModelEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public ModelEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return ModelEnum.fromValue(value);
            }
        }

        public static void validateJsonElement(JsonElement jsonElement) throws IOException {
            String value = jsonElement.getAsString();
            ModelEnum.fromValue(value);
        }
    }

    public static final String SERIALIZED_NAME_MODEL = "model";
    @SerializedName(SERIALIZED_NAME_MODEL)
    @javax.annotation.Nonnull
    private ModelEnum model;

    public EDivisiveDetectionConfig() {
    }

    public EDivisiveDetectionConfig builtIn(@javax.annotation.Nonnull Boolean builtIn) {
        this.builtIn = builtIn;
        return this;
    }

    /**
     * Built In
     *
     * @return builtIn
     */
    @javax.annotation.Nonnull
    public Boolean getBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(@javax.annotation.Nonnull Boolean builtIn) {
        this.builtIn = builtIn;
    }

    public EDivisiveDetectionConfig model(@javax.annotation.Nonnull ModelEnum model) {
        this.model = model;
        return this;
    }

    /**
     * Get model
     *
     * @return model
     */
    @javax.annotation.Nonnull
    public ModelEnum getModel() {
        return model;
    }

    public void setModel(@javax.annotation.Nonnull ModelEnum model) {
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EDivisiveDetectionConfig edivisiveDetectionConfig = (EDivisiveDetectionConfig) o;
        return Objects.equals(this.builtIn, edivisiveDetectionConfig.builtIn) &&
            Objects.equals(this.model, edivisiveDetectionConfig.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(builtIn, model);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EDivisiveDetectionConfig {\n");
        sb.append("    builtIn: ").append(toIndentedString(builtIn)).append("\n");
        sb.append("    model: ").append(toIndentedString(model)).append("\n");
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
        openapiFields.add("builtIn");
        openapiFields.add("model");

        // a set of required properties/fields (JSON key names)
        openapiRequiredFields = new HashSet<String>();
        openapiRequiredFields.add("builtIn");
        openapiRequiredFields.add("model");
    }

    /**
     * Validates the JSON Element and throws an exception if issues found
     *
     * @param jsonElement JSON Element
     * @throws IOException if the JSON Element is invalid with respect to EDivisiveDetectionConfig
     */
    public static void validateJsonElement(JsonElement jsonElement) throws IOException {
        if (jsonElement == null) {
            if (!EDivisiveDetectionConfig.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
                throw new IllegalArgumentException(
                    String.format("The required field(s) %s in EDivisiveDetectionConfig is not found in the empty JSON string",
                        EDivisiveDetectionConfig.openapiRequiredFields.toString()));
            }
        }

        Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
        // check to see if the JSON string contains additional fields
        for (Map.Entry<String, JsonElement> entry : entries) {
            if (!EDivisiveDetectionConfig.openapiFields.contains(entry.getKey())) {
                throw new IllegalArgumentException(
                    String.format("The field `%s` in the JSON string is not defined in the `EDivisiveDetectionConfig` properties. JSON: %s",
                        entry.getKey(), jsonElement.toString()));
            }
        }

        // check to make sure all required properties/fields are present in the JSON string
        for (String requiredField : EDivisiveDetectionConfig.openapiRequiredFields) {
            if (jsonElement.getAsJsonObject().get(requiredField) == null) {
                throw new IllegalArgumentException(
                    String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
            }
        }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        if (!jsonObj.get("model").isJsonPrimitive()) {
            throw new IllegalArgumentException(
                String.format("Expected the field `model` to be a primitive type in the JSON string but got `%s`", jsonObj.get("model").toString()));
        }
        // validate the required field `model`
        ModelEnum.validateJsonElement(jsonObj.get("model"));
    }

    public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!EDivisiveDetectionConfig.class.isAssignableFrom(type.getRawType())) {
                return null; // this class only serializes 'EDivisiveDetectionConfig' and its subtypes
            }
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
            final TypeAdapter<EDivisiveDetectionConfig> thisAdapter
                = gson.getDelegateAdapter(this, TypeToken.get(EDivisiveDetectionConfig.class));

            return (TypeAdapter<T>) new TypeAdapter<EDivisiveDetectionConfig>() {
                @Override
                public void write(JsonWriter out, EDivisiveDetectionConfig value) throws IOException {
                    JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
                    elementAdapter.write(out, obj);
                }

                @Override
                public EDivisiveDetectionConfig read(JsonReader in) throws IOException {
                    JsonElement jsonElement = elementAdapter.read(in);
                    validateJsonElement(jsonElement);
                    return thisAdapter.fromJsonTree(jsonElement);
                }
            }.nullSafe();
        }
    }

    /**
     * Create an instance of EDivisiveDetectionConfig given an JSON string
     *
     * @param jsonString JSON string
     * @return An instance of EDivisiveDetectionConfig
     * @throws IOException if the JSON string is invalid with respect to EDivisiveDetectionConfig
     */
    public static EDivisiveDetectionConfig fromJson(String jsonString) throws IOException {
        return JSON.getGson().fromJson(jsonString, EDivisiveDetectionConfig.class);
    }

    /**
     * Convert an instance of EDivisiveDetectionConfig to an JSON string
     *
     * @return JSON string
     */
    public String toJson() {
        return JSON.getGson().toJson(this);
    }
}

