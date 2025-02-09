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

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Type of backend datastore
 */
@JsonAdapter(DatastoreType.Adapter.class)
public enum DatastoreType {

    POSTGRES("POSTGRES"),

    ELASTICSEARCH("ELASTICSEARCH"),

    COLLECTORAPI("COLLECTORAPI");

    private String value;

    DatastoreType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static DatastoreType fromValue(String value) {
        for (DatastoreType b : DatastoreType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<DatastoreType> {
        @Override
        public void write(final JsonWriter jsonWriter, final DatastoreType enumeration) throws IOException {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public DatastoreType read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return DatastoreType.fromValue(value);
        }
    }

    public static void validateJsonElement(JsonElement jsonElement) throws IOException {
        String value = jsonElement.getAsString();
        DatastoreType.fromValue(value);
    }
}

