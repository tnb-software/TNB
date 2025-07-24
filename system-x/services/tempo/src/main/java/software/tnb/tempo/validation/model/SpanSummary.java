package software.tnb.tempo.validation.model;

import com.fasterxml.jackson.annotation.JsonGetter;

public class SpanSummary {
    String spanID;

    @JsonGetter("spanID")
    public String getSpanID() {
        return spanID;
    }

    public void setSpanID(String spanID) {
        this.spanID = spanID;
    }
}
