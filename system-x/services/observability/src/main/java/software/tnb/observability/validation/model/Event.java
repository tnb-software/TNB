package software.tnb.observability.validation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Event {
    String timeUnixNano;
    String name;
    ArrayList<Attribute> attributes;

    @JsonProperty("timeUnixNano")
    public String getTimeUnixNano() {
        return this.timeUnixNano;
    }

    public void setTimeUnixNano(String timeUnixNano) {
        this.timeUnixNano = timeUnixNano;
    }

    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("attributes")
    public ArrayList<Attribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }
}
