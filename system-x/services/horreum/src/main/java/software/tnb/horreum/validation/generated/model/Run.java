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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Generated;

/**
 * Run
 */
@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]",
    comments = "Generator version: 7.10.0")
public class Run {
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

    public static final String SERIALIZED_NAME_ID = "id";
    @SerializedName(SERIALIZED_NAME_ID)
    @javax.annotation.Nonnull
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

    public static final String SERIALIZED_NAME_METADATA = "metadata";
    @SerializedName(SERIALIZED_NAME_METADATA)
    @javax.annotation.Nullable
    private String metadata;

    public static final String SERIALIZED_NAME_TRASHED = "trashed";
    @SerializedName(SERIALIZED_NAME_TRASHED)
    @javax.annotation.Nonnull
    private Boolean trashed;

    public static final String SERIALIZED_NAME_DATASETS = "datasets";
    @SerializedName(SERIALIZED_NAME_DATASETS)
    @javax.annotation.Nullable
    private List<Dataset> datasets = new ArrayList<>();

    public static final String SERIALIZED_NAME_VALIDATION_ERRORS = "validationErrors";
    @SerializedName(SERIALIZED_NAME_VALIDATION_ERRORS)
    @javax.annotation.Nullable
    private List<ValidationError> validationErrors = new ArrayList<>();

    public Run() {
    }

    public Run access(@javax.annotation.Nonnull Access access) {
        this.access = access;
        return this;
    }

    /**
     * Access rights for the test. This defines the visibility of the Test in the UI
     *
     * @return access
     */
    @javax.annotation.Nonnull
    public Access getAccess() {
        return access;
    }

    public void setAccess(@javax.annotation.Nonnull Access access) {
        this.access = access;
    }

    public Run owner(@javax.annotation.Nonnull String owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Name of the team that owns the test. Users must belong to the team that owns a test to make modifications
     *
     * @return owner
     */
    @javax.annotation.Nonnull
    public String getOwner() {
        return owner;
    }

    public void setOwner(@javax.annotation.Nonnull String owner) {
        this.owner = owner;
    }

    public Run start(@javax.annotation.Nonnull Long start) {
        this.start = start;
        return this;
    }

    /**
     * Run Start timestamp
     *
     * @return start
     */
    @javax.annotation.Nonnull
    public Long getStart() {
        return start;
    }

    public void setStart(@javax.annotation.Nonnull Long start) {
        this.start = start;
    }

    public Run stop(@javax.annotation.Nonnull Long stop) {
        this.stop = stop;
        return this;
    }

    /**
     * Run Stop timestamp
     *
     * @return stop
     */
    @javax.annotation.Nonnull
    public Long getStop() {
        return stop;
    }

    public void setStop(@javax.annotation.Nonnull Long stop) {
        this.stop = stop;
    }

    public Run id(@javax.annotation.Nonnull Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Unique Run ID
     *
     * @return id
     */
    @javax.annotation.Nonnull
    public Integer getId() {
        return id;
    }

    public void setId(@javax.annotation.Nonnull Integer id) {
        this.id = id;
    }

    public Run description(@javax.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Run description
     *
     * @return description
     */
    @javax.annotation.Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@javax.annotation.Nullable String description) {
        this.description = description;
    }

    public Run testid(@javax.annotation.Nonnull Integer testid) {
        this.testid = testid;
        return this;
    }

    /**
     * Test ID run relates to
     *
     * @return testid
     */
    @javax.annotation.Nonnull
    public Integer getTestid() {
        return testid;
    }

    public void setTestid(@javax.annotation.Nonnull Integer testid) {
        this.testid = testid;
    }

    public Run data(@javax.annotation.Nonnull String data) {
        this.data = data;
        return this;
    }

    /**
     * Run result payload
     *
     * @return data
     */
    @javax.annotation.Nonnull
    public String getData() {
        return data;
    }

    public void setData(@javax.annotation.Nonnull String data) {
        this.data = data;
    }

    public Run metadata(@javax.annotation.Nullable String metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * JSON metadata related to run, can be tool configuration etc
     *
     * @return metadata
     */
    @javax.annotation.Nullable
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(@javax.annotation.Nullable String metadata) {
        this.metadata = metadata;
    }

    public Run trashed(@javax.annotation.Nonnull Boolean trashed) {
        this.trashed = trashed;
        return this;
    }

    /**
     * Has Run been deleted from UI
     *
     * @return trashed
     */
    @javax.annotation.Nonnull
    public Boolean getTrashed() {
        return trashed;
    }

    public void setTrashed(@javax.annotation.Nonnull Boolean trashed) {
        this.trashed = trashed;
    }

    public Run datasets(@javax.annotation.Nullable List<Dataset> datasets) {
        this.datasets = datasets;
        return this;
    }

    public Run addDatasetsItem(Dataset datasetsItem) {
        if (this.datasets == null) {
            this.datasets = new ArrayList<>();
        }
        this.datasets.add(datasetsItem);
        return this;
    }

    /**
     * Collection of Datasets derived from Run payload
     *
     * @return datasets
     */
    @javax.annotation.Nullable
    public List<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(@javax.annotation.Nullable List<Dataset> datasets) {
        this.datasets = datasets;
    }

    public Run validationErrors(@javax.annotation.Nullable List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
        return this;
    }

    public Run addValidationErrorsItem(ValidationError validationErrorsItem) {
        if (this.validationErrors == null) {
            this.validationErrors = new ArrayList<>();
        }
        this.validationErrors.add(validationErrorsItem);
        return this;
    }

    /**
     * Collection of Validation Errors in Run payload
     *
     * @return validationErrors
     */
    @javax.annotation.Nullable
    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(@javax.annotation.Nullable List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Run run = (Run) o;
        return Objects.equals(this.access, run.access) &&
            Objects.equals(this.owner, run.owner) &&
            Objects.equals(this.start, run.start) &&
            Objects.equals(this.stop, run.stop) &&
            Objects.equals(this.id, run.id) &&
            Objects.equals(this.description, run.description) &&
            Objects.equals(this.testid, run.testid) &&
            Objects.equals(this.data, run.data) &&
            Objects.equals(this.metadata, run.metadata) &&
            Objects.equals(this.trashed, run.trashed) &&
            Objects.equals(this.datasets, run.datasets) &&
            Objects.equals(this.validationErrors, run.validationErrors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(access, owner, start, stop, id, description, testid, data, metadata, trashed, datasets, validationErrors);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Run {\n");
        sb.append("    access: ").append(toIndentedString(access)).append("\n");
        sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
        sb.append("    start: ").append(toIndentedString(start)).append("\n");
        sb.append("    stop: ").append(toIndentedString(stop)).append("\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    testid: ").append(toIndentedString(testid)).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
        sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
        sb.append("    trashed: ").append(toIndentedString(trashed)).append("\n");
        sb.append("    datasets: ").append(toIndentedString(datasets)).append("\n");
        sb.append("    validationErrors: ").append(toIndentedString(validationErrors)).append("\n");
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
        openapiFields.add("id");
        openapiFields.add("description");
        openapiFields.add("testid");
        openapiFields.add("data");
        openapiFields.add("metadata");
        openapiFields.add("trashed");
        openapiFields.add("datasets");
        openapiFields.add("validationErrors");

        // a set of required properties/fields (JSON key names)
        openapiRequiredFields = new HashSet<String>();
        openapiRequiredFields.add("access");
        openapiRequiredFields.add("owner");
        openapiRequiredFields.add("start");
        openapiRequiredFields.add("stop");
        openapiRequiredFields.add("id");
        openapiRequiredFields.add("testid");
        openapiRequiredFields.add("data");
        openapiRequiredFields.add("trashed");
    }

    /**
     * Validates the JSON Element and throws an exception if issues found
     *
     * @param jsonElement JSON Element
     * @throws IOException if the JSON Element is invalid with respect to Run
     */
    public static void validateJsonElement(JsonElement jsonElement) throws IOException {
        if (jsonElement == null) {
            if (!Run.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
                throw new IllegalArgumentException(
                    String.format("The required field(s) %s in Run is not found in the empty JSON string", Run.openapiRequiredFields.toString()));
            }
        }

        Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
        // check to see if the JSON string contains additional fields
        for (Map.Entry<String, JsonElement> entry : entries) {
            if (!Run.openapiFields.contains(entry.getKey())) {
                throw new IllegalArgumentException(
                    String.format("The field `%s` in the JSON string is not defined in the `Run` properties. JSON: %s", entry.getKey(),
                        jsonElement.toString()));
            }
        }

        // check to make sure all required properties/fields are present in the JSON string
        for (String requiredField : Run.openapiRequiredFields) {
            if (jsonElement.getAsJsonObject().get(requiredField) == null) {
                throw new IllegalArgumentException(
                    String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
            }
        }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        // validate the required field `access`
        Access.validateJsonElement(jsonObj.get("access"));
        if (!jsonObj.get("owner").isJsonPrimitive()) {
            throw new IllegalArgumentException(
                String.format("Expected the field `owner` to be a primitive type in the JSON string but got `%s`", jsonObj.get("owner").toString()));
        }
        if ((jsonObj.get("description") != null && !jsonObj.get("description").isJsonNull()) && !jsonObj.get("description").isJsonPrimitive()) {
            throw new IllegalArgumentException(
                String.format("Expected the field `description` to be a primitive type in the JSON string but got `%s`",
                    jsonObj.get("description").toString()));
        }
        if (!jsonObj.get("data").isJsonPrimitive()) {
            throw new IllegalArgumentException(
                String.format("Expected the field `data` to be a primitive type in the JSON string but got `%s`", jsonObj.get("data").toString()));
        }
        if ((jsonObj.get("metadata") != null && !jsonObj.get("metadata").isJsonNull()) && !jsonObj.get("metadata").isJsonPrimitive()) {
            throw new IllegalArgumentException(String.format("Expected the field `metadata` to be a primitive type in the JSON string but got `%s`",
                jsonObj.get("metadata").toString()));
        }
        if (jsonObj.get("datasets") != null && !jsonObj.get("datasets").isJsonNull()) {
            JsonArray jsonArraydatasets = jsonObj.getAsJsonArray("datasets");
            if (jsonArraydatasets != null) {
                // ensure the json data is an array
                if (!jsonObj.get("datasets").isJsonArray()) {
                    throw new IllegalArgumentException(String.format("Expected the field `datasets` to be an array in the JSON string but got `%s`",
                        jsonObj.get("datasets").toString()));
                }

                // validate the optional field `datasets` (array)
                for (int i = 0; i < jsonArraydatasets.size(); i++) {
                    Dataset.validateJsonElement(jsonArraydatasets.get(i));
                }
                ;
            }
        }
        if (jsonObj.get("validationErrors") != null && !jsonObj.get("validationErrors").isJsonNull()) {
            JsonArray jsonArrayvalidationErrors = jsonObj.getAsJsonArray("validationErrors");
            if (jsonArrayvalidationErrors != null) {
                // ensure the json data is an array
                if (!jsonObj.get("validationErrors").isJsonArray()) {
                    throw new IllegalArgumentException(
                        String.format("Expected the field `validationErrors` to be an array in the JSON string but got `%s`",
                            jsonObj.get("validationErrors").toString()));
                }

                // validate the optional field `validationErrors` (array)
                for (int i = 0; i < jsonArrayvalidationErrors.size(); i++) {
                    ValidationError.validateJsonElement(jsonArrayvalidationErrors.get(i));
                }
                ;
            }
        }
    }

    public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!Run.class.isAssignableFrom(type.getRawType())) {
                return null; // this class only serializes 'Run' and its subtypes
            }
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
            final TypeAdapter<Run> thisAdapter
                = gson.getDelegateAdapter(this, TypeToken.get(Run.class));

            return (TypeAdapter<T>) new TypeAdapter<Run>() {
                @Override
                public void write(JsonWriter out, Run value) throws IOException {
                    JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
                    elementAdapter.write(out, obj);
                }

                @Override
                public Run read(JsonReader in) throws IOException {
                    JsonElement jsonElement = elementAdapter.read(in);
                    validateJsonElement(jsonElement);
                    return thisAdapter.fromJsonTree(jsonElement);
                }
            }.nullSafe();
        }
    }

    /**
     * Create an instance of Run given an JSON string
     *
     * @param jsonString JSON string
     * @return An instance of Run
     * @throws IOException if the JSON string is invalid with respect to Run
     */
    public static Run fromJson(String jsonString) throws IOException {
        return JSON.getGson().fromJson(jsonString, Run.class);
    }

    /**
     * Convert an instance of Run to an JSON string
     *
     * @return JSON string
     */
    public String toJson() {
        return JSON.getGson().toJson(this);
    }
}

