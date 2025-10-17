package software.tnb.tempo.validation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attribute {
    String key;
    Value value;

    @JsonProperty("key")
    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("value")
    public Value getValue() {
        return this.value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
