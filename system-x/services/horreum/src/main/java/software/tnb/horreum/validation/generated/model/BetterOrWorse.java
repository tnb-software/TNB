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
 * Result of running an Experiment
 */
@JsonAdapter(BetterOrWorse.Adapter.class)
public enum BetterOrWorse {

    BETTER("BETTER"),

    SAME("SAME"),

    WORSE("WORSE");

    private String value;

    BetterOrWorse(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static BetterOrWorse fromValue(String value) {
        for (BetterOrWorse b : BetterOrWorse.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<BetterOrWorse> {
        @Override
        public void write(final JsonWriter jsonWriter, final BetterOrWorse enumeration) throws IOException {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public BetterOrWorse read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return BetterOrWorse.fromValue(value);
        }
    }

    public static void validateJsonElement(JsonElement jsonElement) throws IOException {
        String value = jsonElement.getAsString();
        BetterOrWorse.fromValue(value);
    }
}

