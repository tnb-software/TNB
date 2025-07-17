package software.tnb.observability.validation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Scope {
    String name;

    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
