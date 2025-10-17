package software.tnb.tempo.validation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Resource {
    ArrayList<Attribute> attributes;

    @JsonProperty("attributes")
    public ArrayList<Attribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }
}
