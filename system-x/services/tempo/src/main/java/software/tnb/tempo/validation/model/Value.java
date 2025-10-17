package software.tnb.tempo.validation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Value {
    String stringValue;
    ArrayValue arrayValue;
    String intValue;

    @JsonProperty("stringValue")
    public String getStringValue() {
        return this.stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @JsonProperty("arrayValue")
    public ArrayValue getArrayValue() {
        return this.arrayValue;
    }

    public void setArrayValue(ArrayValue arrayValue) {
        this.arrayValue = arrayValue;
    }

    @JsonProperty("intValue")
    public String getIntValue() {
        return this.intValue;
    }

    public void setIntValue(String intValue) {
        this.intValue = intValue;
    }
}
