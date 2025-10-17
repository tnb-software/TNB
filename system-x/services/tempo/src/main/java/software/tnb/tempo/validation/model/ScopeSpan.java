package software.tnb.tempo.validation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ScopeSpan {
    Scope scope;
    ArrayList<Span> spans;

    @JsonProperty("scope")
    public Scope getScope() {
        return this.scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @JsonProperty("spans")
    public ArrayList<Span> getSpans() {
        return this.spans;
    }

    public void setSpans(ArrayList<Span> spans) {
        this.spans = spans;
    }
}
