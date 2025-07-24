package software.tnb.tempo.validation.model;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.List;

public class SpanSet {
    List<SpanSummary> spans;
    Integer matched;

    @JsonGetter("spans")
    public List<SpanSummary> getSpans() {
        return spans;
    }

    public void setSpans(List<SpanSummary> spans) {
        this.spans = spans;
    }

    @JsonGetter("matched")
    public Integer getMatched() {
        return matched;
    }

    public void setMatched(Integer matched) {
        this.matched = matched;
    }
}
