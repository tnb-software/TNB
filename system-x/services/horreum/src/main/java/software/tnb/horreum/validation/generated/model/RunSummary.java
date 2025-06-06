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
 * RunSummary
 */
@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]",
    comments = "Generator version: 7.10.0")
public class RunSummary {
    public static final String SERIALIZED_NAME_ID = "id";
    @SerializedName(SERIALIZED_NAME_ID)
    @javax.annotation.Nonnull
    private Integer id;

    public static final String SERIALIZED_NAME_TESTID = "testid";
    @SerializedName(SERIALIZED_NAME_TESTID)
    @javax.annotation.Nonnull
    private Integer testid;

    public static final String SERIALIZED_NAME_TESTNAME = "testname";
    @SerializedName(SERIALIZED_NAME_TESTNAME)
    @javax.annotation.Nonnull
    private String testname;

    public static final String SERIALIZED_NAME_TRASHED = "trashed";
    @SerializedName(SERIALIZED_NAME_TRASHED)
    @javax.annotation.Nonnull
    private Boolean trashed;

    public static final String SERIALIZED_NAME_HAS_METADATA = "hasMetadata";
    @SerializedName(SERIALIZED_NAME_HAS_METADATA)
    @javax.annotation.Nonnull
    private Boolean hasMetadata;

    public static final String SERIALIZED_NAME_DESCRIPTION = "description";
    @SerializedName(SERIALIZED_NAME_DESCRIPTION)
    @javax.annotation.Nullable
    private String description;

    public static final String SERIALIZED_NAME_SCHEMAS = "schemas";
    @SerializedName(SERIALIZED_NAME_SCHEMAS)
    @javax.annotation.Nullable
    private List<SchemaUsage> schemas = new ArrayList<>();

    public static final String SERIALIZED_NAME_DATASETS = "datasets";
    @SerializedName(SERIALIZED_NAME_DATASETS)
    @javax.annotation.Nonnull
    private List<Integer> datasets = new ArrayList<>();

    public static final String SERIALIZED_NAME_VALIDATION_ERRORS = "validationErrors";
    @SerializedName(SERIALIZED_NAME_VALIDATION_ERRORS)
    @javax.annotation.Nullable
    private List<ValidationError> validationErrors = new ArrayList<>();

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

    public RunSummary() {
    }

    public RunSummary id(@javax.annotation.Nonnull Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Run unique ID
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

    public RunSummary testid(@javax.annotation.Nonnull Integer testid) {
        this.testid = testid;
        return this;
    }

    /**
     * test ID run relates to
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

    public RunSummary testname(@javax.annotation.Nonnull String testname) {
        this.testname = testname;
        return this;
    }

    /**
     * test ID run relates to
     *
     * @return testname
     */
    @javax.annotation.Nonnull
    public String getTestname() {
        return testname;
    }

    public void setTestname(@javax.annotation.Nonnull String testname) {
        this.testname = testname;
    }

    public RunSummary trashed(@javax.annotation.Nonnull Boolean trashed) {
        this.trashed = trashed;
        return this;
    }

    /**
     * has Run been trashed in the UI
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

    public RunSummary hasMetadata(@javax.annotation.Nonnull Boolean hasMetadata) {
        this.hasMetadata = hasMetadata;
        return this;
    }

    /**
     * does Run have metadata uploaded alongside Run data
     *
     * @return hasMetadata
     */
    @javax.annotation.Nonnull
    public Boolean getHasMetadata() {
        return hasMetadata;
    }

    public void setHasMetadata(@javax.annotation.Nonnull Boolean hasMetadata) {
        this.hasMetadata = hasMetadata;
    }

    public RunSummary description(@javax.annotation.Nullable String description) {
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

    public RunSummary schemas(@javax.annotation.Nullable List<SchemaUsage> schemas) {
        this.schemas = schemas;
        return this;
    }

    public RunSummary addSchemasItem(SchemaUsage schemasItem) {
        if (this.schemas == null) {
            this.schemas = new ArrayList<>();
        }
        this.schemas.add(schemasItem);
        return this;
    }

    /**
     * List of all Schema Usages for Run
     *
     * @return schemas
     */
    @javax.annotation.Nullable
    public List<SchemaUsage> getSchemas() {
        return schemas;
    }

    public void setSchemas(@javax.annotation.Nullable List<SchemaUsage> schemas) {
        this.schemas = schemas;
    }

    public RunSummary datasets(@javax.annotation.Nonnull List<Integer> datasets) {
        this.datasets = datasets;
        return this;
    }

    public RunSummary addDatasetsItem(Integer datasetsItem) {
        if (this.datasets == null) {
            this.datasets = new ArrayList<>();
        }
        this.datasets.add(datasetsItem);
        return this;
    }

    /**
     * Array of datasets ids
     *
     * @return datasets
     */
    @javax.annotation.Nonnull
    public List<Integer> getDatasets() {
        return datasets;
    }

    public void setDatasets(@javax.annotation.Nonnull List<Integer> datasets) {
        this.datasets = datasets;
    }

    public RunSummary validationErrors(@javax.annotation.Nullable List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
        return this;
    }

    public RunSummary addValidationErrorsItem(ValidationError validationErrorsItem) {
        if (this.validationErrors == null) {
            this.validationErrors = new ArrayList<>();
        }
        this.validationErrors.add(validationErrorsItem);
        return this;
    }

    /**
     * Array of validation errors
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

    public RunSummary access(@javax.annotation.Nonnull Access access) {
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

    public RunSummary owner(@javax.annotation.Nonnull String owner) {
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

    public RunSummary start(@javax.annotation.Nonnull Long start) {
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

    public RunSummary stop(@javax.annotation.Nonnull Long stop) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RunSummary runSummary = (RunSummary) o;
        return Objects.equals(this.id, runSummary.id) &&
            Objects.equals(this.testid, runSummary.testid) &&
            Objects.equals(this.testname, runSummary.testname) &&
            Objects.equals(this.trashed, runSummary.trashed) &&
            Objects.equals(this.hasMetadata, runSummary.hasMetadata) &&
            Objects.equals(this.description, runSummary.description) &&
            Objects.equals(this.schemas, runSummary.schemas) &&
            Objects.equals(this.datasets, runSummary.datasets) &&
            Objects.equals(this.validationErrors, runSummary.validationErrors) &&
            Objects.equals(this.access, runSummary.access) &&
            Objects.equals(this.owner, runSummary.owner) &&
            Objects.equals(this.start, runSummary.start) &&
            Objects.equals(this.stop, runSummary.stop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, testid, testname, trashed, hasMetadata, description, schemas, datasets, validationErrors, access, owner, start, stop);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RunSummary {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    testid: ").append(toIndentedString(testid)).append("\n");
        sb.append("    testname: ").append(toIndentedString(testname)).append("\n");
        sb.append("    trashed: ").append(toIndentedString(trashed)).append("\n");
        sb.append("    hasMetadata: ").append(toIndentedString(hasMetadata)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    schemas: ").append(toIndentedString(schemas)).append("\n");
        sb.append("    datasets: ").append(toIndentedString(datasets)).append("\n");
        sb.append("    validationErrors: ").append(toIndentedString(validationErrors)).append("\n");
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
        openapiRequiredFields.add("id");
        openapiRequiredFields.add("testid");
        openapiRequiredFields.add("testname");
        openapiRequiredFields.add("trashed");
        openapiRequiredFields.add("hasMetadata");
        openapiRequiredFields.add("datasets");
        openapiRequiredFields.add("access");
        openapiRequiredFields.add("owner");
        openapiRequiredFields.add("start");
        openapiRequiredFields.add("stop");
    }

    /**
     * Validates the JSON Element and throws an exception if issues found
     *
     * @param jsonElement JSON Element
     * @throws IOException if the JSON Element is invalid with respect to RunSummary
     */
    public static void validateJsonElement(JsonElement jsonElement) throws IOException {
        if (jsonElement == null) {
            if (!RunSummary.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
                throw new IllegalArgumentException(String.format("The required field(s) %s in RunSummary is not found in the empty JSON string",
                    RunSummary.openapiRequiredFields.toString()));
            }
        }

        Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
        // check to see if the JSON string contains additional fields
        for (Map.Entry<String, JsonElement> entry : entries) {
            if (!RunSummary.openapiFields.contains(entry.getKey())) {
                throw new IllegalArgumentException(
                    String.format("The field `%s` in the JSON string is not defined in the `RunSummary` properties. JSON: %s", entry.getKey(),
                        jsonElement.toString()));
            }
        }

        // check to make sure all required properties/fields are present in the JSON string
        for (String requiredField : RunSummary.openapiRequiredFields) {
            if (jsonElement.getAsJsonObject().get(requiredField) == null) {
                throw new IllegalArgumentException(
                    String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
            }
        }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        if (!jsonObj.get("testname").isJsonPrimitive()) {
            throw new IllegalArgumentException(String.format("Expected the field `testname` to be a primitive type in the JSON string but got `%s`",
                jsonObj.get("testname").toString()));
        }
        if ((jsonObj.get("description") != null && !jsonObj.get("description").isJsonNull()) && !jsonObj.get("description").isJsonPrimitive()) {
            throw new IllegalArgumentException(
                String.format("Expected the field `description` to be a primitive type in the JSON string but got `%s`",
                    jsonObj.get("description").toString()));
        }
        if (jsonObj.get("schemas") != null && !jsonObj.get("schemas").isJsonNull()) {
            JsonArray jsonArrayschemas = jsonObj.getAsJsonArray("schemas");
            if (jsonArrayschemas != null) {
                // ensure the json data is an array
                if (!jsonObj.get("schemas").isJsonArray()) {
                    throw new IllegalArgumentException(String.format("Expected the field `schemas` to be an array in the JSON string but got `%s`",
                        jsonObj.get("schemas").toString()));
                }

                // validate the optional field `schemas` (array)
                for (int i = 0; i < jsonArrayschemas.size(); i++) {
                    SchemaUsage.validateJsonElement(jsonArrayschemas.get(i));
                }
                ;
            }
        }
        // ensure the required json array is present
        if (jsonObj.get("datasets") == null) {
            throw new IllegalArgumentException("Expected the field `linkedContent` to be an array in the JSON string but got `null`");
        } else if (!jsonObj.get("datasets").isJsonArray()) {
            throw new IllegalArgumentException(
                String.format("Expected the field `datasets` to be an array in the JSON string but got `%s`", jsonObj.get("datasets").toString()));
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
        // validate the required field `access`
        Access.validateJsonElement(jsonObj.get("access"));
        if (!jsonObj.get("owner").isJsonPrimitive()) {
            throw new IllegalArgumentException(
                String.format("Expected the field `owner` to be a primitive type in the JSON string but got `%s`", jsonObj.get("owner").toString()));
        }
    }

    public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!RunSummary.class.isAssignableFrom(type.getRawType())) {
                return null; // this class only serializes 'RunSummary' and its subtypes
            }
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
            final TypeAdapter<RunSummary> thisAdapter
                = gson.getDelegateAdapter(this, TypeToken.get(RunSummary.class));

            return (TypeAdapter<T>) new TypeAdapter<RunSummary>() {
                @Override
                public void write(JsonWriter out, RunSummary value) throws IOException {
                    JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
                    elementAdapter.write(out, obj);
                }

                @Override
                public RunSummary read(JsonReader in) throws IOException {
                    JsonElement jsonElement = elementAdapter.read(in);
                    validateJsonElement(jsonElement);
                    return thisAdapter.fromJsonTree(jsonElement);
                }
            }.nullSafe();
        }
    }

    /**
     * Create an instance of RunSummary given an JSON string
     *
     * @param jsonString JSON string
     * @return An instance of RunSummary
     * @throws IOException if the JSON string is invalid with respect to RunSummary
     */
    public static RunSummary fromJson(String jsonString) throws IOException {
        return JSON.getGson().fromJson(jsonString, RunSummary.class);
    }

    /**
     * Convert an instance of RunSummary to an JSON string
     *
     * @return JSON string
     */
    public String toJson() {
        return JSON.getGson().toJson(this);
    }
}

