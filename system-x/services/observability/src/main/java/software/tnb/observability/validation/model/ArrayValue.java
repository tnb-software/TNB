package software.tnb.observability.validation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ArrayValue {

    ArrayList<Value> values;

    @JsonProperty("values")
    public ArrayList<Value> getValues() {
        return this.values;
    }

    public void setValues(ArrayList<Value> values) {
        this.values = values;
    }
}
