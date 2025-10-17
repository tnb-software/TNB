package software.tnb.tempo.validation.model;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.List;

public class SearchResult {
    List<FoundTrace> traces;

    @JsonGetter("traces")
    public List<FoundTrace> getTraces() {
        return traces;
    }

    public void setTraces(List<FoundTrace> traces) {
        this.traces = traces;
    }
}
