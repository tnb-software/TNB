package software.tnb.observability.validation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Batch {
    Resource resource;
    ArrayList<ScopeSpan> scopeSpans;

    @JsonProperty("resource")
    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @JsonProperty("scopeSpans")
    public ArrayList<ScopeSpan> getScopeSpans() {
        return this.scopeSpans;
    }

    public void setScopeSpans(ArrayList<ScopeSpan> scopeSpans) {
        this.scopeSpans = scopeSpans;
    }
}
