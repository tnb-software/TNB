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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonElement;

import software.tnb.horreum.validation.generated.JSON;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-12-02T20:53:38.158166061+01:00[Europe/Bratislava]", comments = "Generator version: 7.10.0")
public class DatastoreConfig extends AbstractOpenApiSchema {
    private static final Logger log = Logger.getLogger(DatastoreConfig.class.getName());

    public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!DatastoreConfig.class.isAssignableFrom(type.getRawType())) {
                return null; // this class only serializes 'DatastoreConfig' and its subtypes
            }
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
            final TypeAdapter<ElasticsearchDatastoreConfig> adapterElasticsearchDatastoreConfig = gson.getDelegateAdapter(this, TypeToken.get(ElasticsearchDatastoreConfig.class));
            final TypeAdapter<PostgresDatastoreConfig> adapterPostgresDatastoreConfig = gson.getDelegateAdapter(this, TypeToken.get(PostgresDatastoreConfig.class));

            return (TypeAdapter<T>) new TypeAdapter<DatastoreConfig>() {
                @Override
                public void write(JsonWriter out, DatastoreConfig value) throws IOException {
                    if (value == null || value.getActualInstance() == null) {
                        elementAdapter.write(out, null);
                        return;
                    }

                    // check if the actual instance is of the type `ElasticsearchDatastoreConfig`
                    if (value.getActualInstance() instanceof ElasticsearchDatastoreConfig) {
                        JsonElement element = adapterElasticsearchDatastoreConfig.toJsonTree((ElasticsearchDatastoreConfig)value.getActualInstance());
                        elementAdapter.write(out, element);
                        return;
                    }
                    // check if the actual instance is of the type `PostgresDatastoreConfig`
                    if (value.getActualInstance() instanceof PostgresDatastoreConfig) {
                        JsonElement element = adapterPostgresDatastoreConfig.toJsonTree((PostgresDatastoreConfig)value.getActualInstance());
                        elementAdapter.write(out, element);
                        return;
                    }
                    throw new IOException("Failed to serialize as the type doesn't match oneOf schemas: ElasticsearchDatastoreConfig, PostgresDatastoreConfig");
                }

                @Override
                public DatastoreConfig read(JsonReader in) throws IOException {
                    Object deserialized = null;
                    JsonElement jsonElement = elementAdapter.read(in);

                    int match = 0;
                    ArrayList<String> errorMessages = new ArrayList<>();
                    TypeAdapter actualAdapter = elementAdapter;

                    // deserialize ElasticsearchDatastoreConfig
                    try {
                        // validate the JSON object to see if any exception is thrown
                        ElasticsearchDatastoreConfig.validateJsonElement(jsonElement);
                        actualAdapter = adapterElasticsearchDatastoreConfig;
                        match++;
                        log.log(Level.FINER, "Input data matches schema 'ElasticsearchDatastoreConfig'");
                    } catch (Exception e) {
                        // deserialization failed, continue
                        errorMessages.add(String.format("Deserialization for ElasticsearchDatastoreConfig failed with `%s`.", e.getMessage()));
                        log.log(Level.FINER, "Input data does not match schema 'ElasticsearchDatastoreConfig'", e);
                    }
                    // deserialize PostgresDatastoreConfig
                    try {
                        // validate the JSON object to see if any exception is thrown
                        PostgresDatastoreConfig.validateJsonElement(jsonElement);
                        actualAdapter = adapterPostgresDatastoreConfig;
                        match++;
                        log.log(Level.FINER, "Input data matches schema 'PostgresDatastoreConfig'");
                    } catch (Exception e) {
                        // deserialization failed, continue
                        errorMessages.add(String.format("Deserialization for PostgresDatastoreConfig failed with `%s`.", e.getMessage()));
                        log.log(Level.FINER, "Input data does not match schema 'PostgresDatastoreConfig'", e);
                    }

                    if (match == 1) {
                        DatastoreConfig ret = new DatastoreConfig();
                        ret.setActualInstance(actualAdapter.fromJsonTree(jsonElement));
                        return ret;
                    }

                    throw new IOException(String.format("Failed deserialization for DatastoreConfig: %d classes match result, expected 1. Detailed failure message for oneOf schemas: %s. JSON: %s", match, errorMessages, jsonElement.toString()));
                }
            }.nullSafe();
        }
    }

    // store a list of schema names defined in oneOf
    public static final Map<String, Class<?>> schemas = new HashMap<String, Class<?>>();

    public DatastoreConfig() {
        super("oneOf", Boolean.FALSE);
    }

    public DatastoreConfig(Object o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    static {
        schemas.put("ElasticsearchDatastoreConfig", ElasticsearchDatastoreConfig.class);
        schemas.put("PostgresDatastoreConfig", PostgresDatastoreConfig.class);
    }

    @Override
    public Map<String, Class<?>> getSchemas() {
        return DatastoreConfig.schemas;
    }

    /**
     * Set the instance that matches the oneOf child schema, check
     * the instance parameter is valid against the oneOf child schemas:
     * ElasticsearchDatastoreConfig, PostgresDatastoreConfig
     *
     * It could be an instance of the 'oneOf' schemas.
     */
    @Override
    public void setActualInstance(Object instance) {
        if (instance instanceof ElasticsearchDatastoreConfig) {
            super.setActualInstance(instance);
            return;
        }

        if (instance instanceof PostgresDatastoreConfig) {
            super.setActualInstance(instance);
            return;
        }

        throw new RuntimeException("Invalid instance type. Must be ElasticsearchDatastoreConfig, PostgresDatastoreConfig");
    }

    /**
     * Get the actual instance, which can be the following:
     * ElasticsearchDatastoreConfig, PostgresDatastoreConfig
     *
     * @return The actual instance (ElasticsearchDatastoreConfig, PostgresDatastoreConfig)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object getActualInstance() {
        return super.getActualInstance();
    }

    /**
     * Get the actual instance of `ElasticsearchDatastoreConfig`. If the actual instance is not `ElasticsearchDatastoreConfig`,
     * the ClassCastException will be thrown.
     *
     * @return The actual instance of `ElasticsearchDatastoreConfig`
     * @throws ClassCastException if the instance is not `ElasticsearchDatastoreConfig`
     */
    public ElasticsearchDatastoreConfig getElasticsearchDatastoreConfig() throws ClassCastException {
        return (ElasticsearchDatastoreConfig)super.getActualInstance();
    }

    /**
     * Get the actual instance of `PostgresDatastoreConfig`. If the actual instance is not `PostgresDatastoreConfig`,
     * the ClassCastException will be thrown.
     *
     * @return The actual instance of `PostgresDatastoreConfig`
     * @throws ClassCastException if the instance is not `PostgresDatastoreConfig`
     */
    public PostgresDatastoreConfig getPostgresDatastoreConfig() throws ClassCastException {
        return (PostgresDatastoreConfig)super.getActualInstance();
    }

    /**
     * Validates the JSON Element and throws an exception if issues found
     *
     * @param jsonElement JSON Element
     * @throws IOException if the JSON Element is invalid with respect to DatastoreConfig
     */
    public static void validateJsonElement(JsonElement jsonElement) throws IOException {
        // validate oneOf schemas one by one
        int validCount = 0;
        ArrayList<String> errorMessages = new ArrayList<>();
        // validate the json string with ElasticsearchDatastoreConfig
        try {
            ElasticsearchDatastoreConfig.validateJsonElement(jsonElement);
            validCount++;
        } catch (Exception e) {
            errorMessages.add(String.format("Deserialization for ElasticsearchDatastoreConfig failed with `%s`.", e.getMessage()));
            // continue to the next one
        }
        // validate the json string with PostgresDatastoreConfig
        try {
            PostgresDatastoreConfig.validateJsonElement(jsonElement);
            validCount++;
        } catch (Exception e) {
            errorMessages.add(String.format("Deserialization for PostgresDatastoreConfig failed with `%s`.", e.getMessage()));
            // continue to the next one
        }
        if (validCount != 1) {
            throw new IOException(String.format("The JSON string is invalid for DatastoreConfig with oneOf schemas: ElasticsearchDatastoreConfig, PostgresDatastoreConfig. %d class(es) match the result, expected 1. Detailed failure message for oneOf schemas: %s. JSON: %s", validCount, errorMessages, jsonElement.toString()));
        }
    }

    /**
     * Create an instance of DatastoreConfig given an JSON string
     *
     * @param jsonString JSON string
     * @return An instance of DatastoreConfig
     * @throws IOException if the JSON string is invalid with respect to DatastoreConfig
     */
    public static DatastoreConfig fromJson(String jsonString) throws IOException {
        return JSON.getGson().fromJson(jsonString, DatastoreConfig.class);
    }

    /**
     * Convert an instance of DatastoreConfig to an JSON string
     *
     * @return JSON string
     */
    public String toJson() {
        return JSON.getGson().toJson(this);
    }
}

