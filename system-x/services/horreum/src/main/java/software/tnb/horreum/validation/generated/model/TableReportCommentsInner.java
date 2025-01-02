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
 * TableReportCommentsInner
 */
@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]",
    comments = "Generator version: 7.10.0")
public class TableReportCommentsInner {
    public static final String SERIALIZED_NAME_ID = "id";
    @SerializedName(SERIALIZED_NAME_ID)
    @javax.annotation.Nullable
    private Integer id;

    public static final String SERIALIZED_NAME_LEVEL = "level";
    @SerializedName(SERIALIZED_NAME_LEVEL)
    @javax.annotation.Nonnull
    private Integer level;

    public static final String SERIALIZED_NAME_CATEGORY = "category";
    @SerializedName(SERIALIZED_NAME_CATEGORY)
    @javax.annotation.Nullable
    private String category;

    public static final String SERIALIZED_NAME_COMPONENT_ID = "componentId";
    @SerializedName(SERIALIZED_NAME_COMPONENT_ID)
    @javax.annotation.Nullable
    private Integer componentId;

    public static final String SERIALIZED_NAME_COMMENT = "comment";
    @SerializedName(SERIALIZED_NAME_COMMENT)
    @javax.annotation.Nonnull
    private String comment;

    public TableReportCommentsInner() {
    }

    public TableReportCommentsInner id(@javax.annotation.Nullable Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     */
    @javax.annotation.Nullable
    public Integer getId() {
        return id;
    }

    public void setId(@javax.annotation.Nullable Integer id) {
        this.id = id;
    }

    public TableReportCommentsInner level(@javax.annotation.Nonnull Integer level) {
        this.level = level;
        return this;
    }

    /**
     * Get level
     *
     * @return level
     */
    @javax.annotation.Nonnull
    public Integer getLevel() {
        return level;
    }

    public void setLevel(@javax.annotation.Nonnull Integer level) {
        this.level = level;
    }

    public TableReportCommentsInner category(@javax.annotation.Nullable String category) {
        this.category = category;
        return this;
    }

    /**
     * Get category
     *
     * @return category
     */
    @javax.annotation.Nullable
    public String getCategory() {
        return category;
    }

    public void setCategory(@javax.annotation.Nullable String category) {
        this.category = category;
    }

    public TableReportCommentsInner componentId(@javax.annotation.Nullable Integer componentId) {
        this.componentId = componentId;
        return this;
    }

    /**
     * Get componentId
     *
     * @return componentId
     */
    @javax.annotation.Nullable
    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(@javax.annotation.Nullable Integer componentId) {
        this.componentId = componentId;
    }

    public TableReportCommentsInner comment(@javax.annotation.Nonnull String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Get comment
     *
     * @return comment
     */
    @javax.annotation.Nonnull
    public String getComment() {
        return comment;
    }

    public void setComment(@javax.annotation.Nonnull String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableReportCommentsInner tableReportCommentsInner = (TableReportCommentsInner) o;
        return Objects.equals(this.id, tableReportCommentsInner.id) &&
            Objects.equals(this.level, tableReportCommentsInner.level) &&
            Objects.equals(this.category, tableReportCommentsInner.category) &&
            Objects.equals(this.componentId, tableReportCommentsInner.componentId) &&
            Objects.equals(this.comment, tableReportCommentsInner.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, level, category, componentId, comment);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TableReportCommentsInner {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    level: ").append(toIndentedString(level)).append("\n");
        sb.append("    category: ").append(toIndentedString(category)).append("\n");
        sb.append("    componentId: ").append(toIndentedString(componentId)).append("\n");
        sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
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
        openapiFields.add("category");
        openapiFields.add("componentId");
        openapiFields.add("comment");

        // a set of required properties/fields (JSON key names)
        openapiRequiredFields = new HashSet<String>();
        openapiRequiredFields.add("level");
        openapiRequiredFields.add("comment");
    }

    /**
     * Validates the JSON Element and throws an exception if issues found
     *
     * @param jsonElement JSON Element
     * @throws IOException if the JSON Element is invalid with respect to TableReportCommentsInner
     */
    public static void validateJsonElement(JsonElement jsonElement) throws IOException {
        if (jsonElement == null) {
            if (!TableReportCommentsInner.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
                throw new IllegalArgumentException(
                    String.format("The required field(s) %s in TableReportCommentsInner is not found in the empty JSON string",
                        TableReportCommentsInner.openapiRequiredFields.toString()));
            }
        }

        Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
        // check to see if the JSON string contains additional fields
        for (Map.Entry<String, JsonElement> entry : entries) {
            if (!TableReportCommentsInner.openapiFields.contains(entry.getKey())) {
                throw new IllegalArgumentException(
                    String.format("The field `%s` in the JSON string is not defined in the `TableReportCommentsInner` properties. JSON: %s",
                        entry.getKey(), jsonElement.toString()));
            }
        }

        // check to make sure all required properties/fields are present in the JSON string
        for (String requiredField : TableReportCommentsInner.openapiRequiredFields) {
            if (jsonElement.getAsJsonObject().get(requiredField) == null) {
                throw new IllegalArgumentException(
                    String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
            }
        }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        if ((jsonObj.get("category") != null && !jsonObj.get("category").isJsonNull()) && !jsonObj.get("category").isJsonPrimitive()) {
            throw new IllegalArgumentException(String.format("Expected the field `category` to be a primitive type in the JSON string but got `%s`",
                jsonObj.get("category").toString()));
        }
        if (!jsonObj.get("comment").isJsonPrimitive()) {
            throw new IllegalArgumentException(String.format("Expected the field `comment` to be a primitive type in the JSON string but got `%s`",
                jsonObj.get("comment").toString()));
        }
    }

    public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!TableReportCommentsInner.class.isAssignableFrom(type.getRawType())) {
                return null; // this class only serializes 'TableReportCommentsInner' and its subtypes
            }
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
            final TypeAdapter<TableReportCommentsInner> thisAdapter
                = gson.getDelegateAdapter(this, TypeToken.get(TableReportCommentsInner.class));

            return (TypeAdapter<T>) new TypeAdapter<TableReportCommentsInner>() {
                @Override
                public void write(JsonWriter out, TableReportCommentsInner value) throws IOException {
                    JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
                    elementAdapter.write(out, obj);
                }

                @Override
                public TableReportCommentsInner read(JsonReader in) throws IOException {
                    JsonElement jsonElement = elementAdapter.read(in);
                    validateJsonElement(jsonElement);
                    return thisAdapter.fromJsonTree(jsonElement);
                }
            }.nullSafe();
        }
    }

    /**
     * Create an instance of TableReportCommentsInner given an JSON string
     *
     * @param jsonString JSON string
     * @return An instance of TableReportCommentsInner
     * @throws IOException if the JSON string is invalid with respect to TableReportCommentsInner
     */
    public static TableReportCommentsInner fromJson(String jsonString) throws IOException {
        return JSON.getGson().fromJson(jsonString, TableReportCommentsInner.class);
    }

    /**
     * Convert an instance of TableReportCommentsInner to an JSON string
     *
     * @return JSON string
     */
    public String toJson() {
        return JSON.getGson().toJson(this);
    }
}

