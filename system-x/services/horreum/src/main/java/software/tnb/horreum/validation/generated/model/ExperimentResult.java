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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Generated;

/**
 * Result of running an Experiment
 */
@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]",
    comments = "Generator version: 7.10.0")
public class ExperimentResult {
    public static final String SERIALIZED_NAME_PROFILE = "profile";
    @SerializedName(SERIALIZED_NAME_PROFILE)
    @javax.annotation.Nullable
    private ExperimentProfile profile;

    public static final String SERIALIZED_NAME_LOGS = "logs";
    @SerializedName(SERIALIZED_NAME_LOGS)
    @javax.annotation.Nullable
    private List<DatasetLog> logs = new ArrayList<>();

    public static final String SERIALIZED_NAME_DATASET_INFO = "datasetInfo";
    @SerializedName(SERIALIZED_NAME_DATASET_INFO)
    @javax.annotation.Nullable
    private DatasetInfo datasetInfo;

    public static final String SERIALIZED_NAME_BASELINE = "baseline";
    @SerializedName(SERIALIZED_NAME_BASELINE)
    @javax.annotation.Nullable
    private List<DatasetInfo> baseline = new ArrayList<>();

    public static final String SERIALIZED_NAME_RESULTS = "results";
    @SerializedName(SERIALIZED_NAME_RESULTS)
    @javax.annotation.Nullable
    private Map<String, ComparisonResult> results = new HashMap<>();

    public static final String SERIALIZED_NAME_EXTRA_LABELS = "extraLabels";
    @SerializedName(SERIALIZED_NAME_EXTRA_LABELS)
    @javax.annotation.Nullable
    private String extraLabels;

    public static final String SERIALIZED_NAME_NOTIFY = "notify";
    @SerializedName(SERIALIZED_NAME_NOTIFY)
    @javax.annotation.Nullable
    private Boolean notify;

    public ExperimentResult() {
    }

    public ExperimentResult profile(@javax.annotation.Nullable ExperimentProfile profile) {
        this.profile = profile;
        return this;
    }

    /**
     * Experiment profile that results relates to
     *
     * @return profile
     */
    @javax.annotation.Nullable
    public ExperimentProfile getProfile() {
        return profile;
    }

    public void setProfile(@javax.annotation.Nullable ExperimentProfile profile) {
        this.profile = profile;
    }

    public ExperimentResult logs(@javax.annotation.Nullable List<DatasetLog> logs) {
        this.logs = logs;
        return this;
    }

    public ExperimentResult addLogsItem(DatasetLog logsItem) {
        if (this.logs == null) {
            this.logs = new ArrayList<>();
        }
        this.logs.add(logsItem);
        return this;
    }

    /**
     * A list of log statements recorded while Experiment was evaluated
     *
     * @return logs
     */
    @javax.annotation.Nullable
    public List<DatasetLog> getLogs() {
        return logs;
    }

    public void setLogs(@javax.annotation.Nullable List<DatasetLog> logs) {
        this.logs = logs;
    }

    public ExperimentResult datasetInfo(@javax.annotation.Nullable DatasetInfo datasetInfo) {
        this.datasetInfo = datasetInfo;
        return this;
    }

    /**
     * Dataset Info about dataset used for experiment
     *
     * @return datasetInfo
     */
    @javax.annotation.Nullable
    public DatasetInfo getDatasetInfo() {
        return datasetInfo;
    }

    public void setDatasetInfo(@javax.annotation.Nullable DatasetInfo datasetInfo) {
        this.datasetInfo = datasetInfo;
    }

    public ExperimentResult baseline(@javax.annotation.Nullable List<DatasetInfo> baseline) {
        this.baseline = baseline;
        return this;
    }

    public ExperimentResult addBaselineItem(DatasetInfo baselineItem) {
        if (this.baseline == null) {
            this.baseline = new ArrayList<>();
        }
        this.baseline.add(baselineItem);
        return this;
    }

    /**
     * A list of Dataset Info for experiment baseline(s)
     *
     * @return baseline
     */
    @javax.annotation.Nullable
    public List<DatasetInfo> getBaseline() {
        return baseline;
    }

    public void setBaseline(@javax.annotation.Nullable List<DatasetInfo> baseline) {
        this.baseline = baseline;
    }

    public ExperimentResult results(@javax.annotation.Nullable Map<String, ComparisonResult> results) {
        this.results = results;
        return this;
    }

    public ExperimentResult putResultsItem(String key, ComparisonResult resultsItem) {
        if (this.results == null) {
            this.results = new HashMap<>();
        }
        this.results.put(key, resultsItem);
        return this;
    }

    /**
     * A Map of all comparisons and results evaluated during an Experiment
     *
     * @return results
     */
    @javax.annotation.Nullable
    public Map<String, ComparisonResult> getResults() {
        return results;
    }

    public void setResults(@javax.annotation.Nullable Map<String, ComparisonResult> results) {
        this.results = results;
    }

    public ExperimentResult extraLabels(@javax.annotation.Nullable String extraLabels) {
        this.extraLabels = extraLabels;
        return this;
    }

    /**
     * Get extraLabels
     *
     * @return extraLabels
     */
    @javax.annotation.Nullable
    public String getExtraLabels() {
        return extraLabels;
    }

    public void setExtraLabels(@javax.annotation.Nullable String extraLabels) {
        this.extraLabels = extraLabels;
    }

    public ExperimentResult notify(@javax.annotation.Nullable Boolean notify) {
        this.notify = notify;
        return this;
    }

    /**
     * Get notify
     *
     * @return notify
     */
    @javax.annotation.Nullable
    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(@javax.annotation.Nullable Boolean notify) {
        this.notify = notify;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExperimentResult experimentResult = (ExperimentResult) o;
        return Objects.equals(this.profile, experimentResult.profile) &&
            Objects.equals(this.logs, experimentResult.logs) &&
            Objects.equals(this.datasetInfo, experimentResult.datasetInfo) &&
            Objects.equals(this.baseline, experimentResult.baseline) &&
            Objects.equals(this.results, experimentResult.results) &&
            Objects.equals(this.extraLabels, experimentResult.extraLabels) &&
            Objects.equals(this.notify, experimentResult.notify);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile, logs, datasetInfo, baseline, results, extraLabels, notify);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ExperimentResult {\n");
        sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
        sb.append("    logs: ").append(toIndentedString(logs)).append("\n");
        sb.append("    datasetInfo: ").append(toIndentedString(datasetInfo)).append("\n");
        sb.append("    baseline: ").append(toIndentedString(baseline)).append("\n");
        sb.append("    results: ").append(toIndentedString(results)).append("\n");
        sb.append("    extraLabels: ").append(toIndentedString(extraLabels)).append("\n");
        sb.append("    notify: ").append(toIndentedString(notify)).append("\n");
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
        openapiFields.add("profile");
        openapiFields.add("logs");
        openapiFields.add("datasetInfo");
        openapiFields.add("baseline");
        openapiFields.add("results");
        openapiFields.add("extraLabels");
        openapiFields.add("notify");

        // a set of required properties/fields (JSON key names)
        openapiRequiredFields = new HashSet<String>();
    }

    /**
     * Validates the JSON Element and throws an exception if issues found
     *
     * @param jsonElement JSON Element
     * @throws IOException if the JSON Element is invalid with respect to ExperimentResult
     */
    public static void validateJsonElement(JsonElement jsonElement) throws IOException {
        if (jsonElement == null) {
            if (!ExperimentResult.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
                throw new IllegalArgumentException(String.format("The required field(s) %s in ExperimentResult is not found in the empty JSON string",
                    ExperimentResult.openapiRequiredFields.toString()));
            }
        }

        Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
        // check to see if the JSON string contains additional fields
        for (Map.Entry<String, JsonElement> entry : entries) {
            if (!ExperimentResult.openapiFields.contains(entry.getKey())) {
                throw new IllegalArgumentException(
                    String.format("The field `%s` in the JSON string is not defined in the `ExperimentResult` properties. JSON: %s", entry.getKey(),
                        jsonElement.toString()));
            }
        }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        // validate the optional field `profile`
        if (jsonObj.get("profile") != null && !jsonObj.get("profile").isJsonNull()) {
            ExperimentProfile.validateJsonElement(jsonObj.get("profile"));
        }
        if (jsonObj.get("logs") != null && !jsonObj.get("logs").isJsonNull()) {
            JsonArray jsonArraylogs = jsonObj.getAsJsonArray("logs");
            if (jsonArraylogs != null) {
                // ensure the json data is an array
                if (!jsonObj.get("logs").isJsonArray()) {
                    throw new IllegalArgumentException(
                        String.format("Expected the field `logs` to be an array in the JSON string but got `%s`", jsonObj.get("logs").toString()));
                }

                // validate the optional field `logs` (array)
                for (int i = 0; i < jsonArraylogs.size(); i++) {
                    DatasetLog.validateJsonElement(jsonArraylogs.get(i));
                }
                ;
            }
        }
        // validate the optional field `datasetInfo`
        if (jsonObj.get("datasetInfo") != null && !jsonObj.get("datasetInfo").isJsonNull()) {
            DatasetInfo.validateJsonElement(jsonObj.get("datasetInfo"));
        }
        if (jsonObj.get("baseline") != null && !jsonObj.get("baseline").isJsonNull()) {
            JsonArray jsonArraybaseline = jsonObj.getAsJsonArray("baseline");
            if (jsonArraybaseline != null) {
                // ensure the json data is an array
                if (!jsonObj.get("baseline").isJsonArray()) {
                    throw new IllegalArgumentException(String.format("Expected the field `baseline` to be an array in the JSON string but got `%s`",
                        jsonObj.get("baseline").toString()));
                }

                // validate the optional field `baseline` (array)
                for (int i = 0; i < jsonArraybaseline.size(); i++) {
                    DatasetInfo.validateJsonElement(jsonArraybaseline.get(i));
                }
                ;
            }
        }
        if ((jsonObj.get("extraLabels") != null && !jsonObj.get("extraLabels").isJsonNull()) && !jsonObj.get("extraLabels").isJsonPrimitive()) {
            throw new IllegalArgumentException(
                String.format("Expected the field `extraLabels` to be a primitive type in the JSON string but got `%s`",
                    jsonObj.get("extraLabels").toString()));
        }
    }

    public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!ExperimentResult.class.isAssignableFrom(type.getRawType())) {
                return null; // this class only serializes 'ExperimentResult' and its subtypes
            }
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
            final TypeAdapter<ExperimentResult> thisAdapter
                = gson.getDelegateAdapter(this, TypeToken.get(ExperimentResult.class));

            return (TypeAdapter<T>) new TypeAdapter<ExperimentResult>() {
                @Override
                public void write(JsonWriter out, ExperimentResult value) throws IOException {
                    JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
                    elementAdapter.write(out, obj);
                }

                @Override
                public ExperimentResult read(JsonReader in) throws IOException {
                    JsonElement jsonElement = elementAdapter.read(in);
                    validateJsonElement(jsonElement);
                    return thisAdapter.fromJsonTree(jsonElement);
                }
            }.nullSafe();
        }
    }

    /**
     * Create an instance of ExperimentResult given an JSON string
     *
     * @param jsonString JSON string
     * @return An instance of ExperimentResult
     * @throws IOException if the JSON string is invalid with respect to ExperimentResult
     */
    public static ExperimentResult fromJson(String jsonString) throws IOException {
        return JSON.getGson().fromJson(jsonString, ExperimentResult.class);
    }

    /**
     * Convert an instance of ExperimentResult to an JSON string
     *
     * @return JSON string
     */
    public String toJson() {
        return JSON.getGson().toJson(this);
    }
}
