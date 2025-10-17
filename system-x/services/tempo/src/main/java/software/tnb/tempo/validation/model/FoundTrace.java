package software.tnb.tempo.validation.model;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.List;
import java.util.Map;

public class FoundTrace {
    String traceID;
    String rootServiceName;
    String rootTraceName;
    SpanSet spanSet;
    List<SpanSet> spanSets;
    Map<String, Object> serviceStats;

    @JsonGetter("traceID")
    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    @JsonGetter("rootServiceName")
    public String getRootServiceName() {
        return rootServiceName;
    }

    public void setRootServiceName(String rootServiceName) {
        this.rootServiceName = rootServiceName;
    }

    @JsonGetter("rootTraceName")
    public String getRootTraceName() {
        return rootTraceName;
    }

    public void setRootTraceName(String rootTraceName) {
        this.rootTraceName = rootTraceName;
    }

    @JsonGetter("spanSet")
    public SpanSet getSpanSet() {
        return spanSet;
    }

    public void setSpanSet(SpanSet spanSet) {
        this.spanSet = spanSet;
    }

    @JsonGetter("spanSets")
    public List<SpanSet> getSpanSets() {
        return spanSets;
    }

    public void setSpanSets(List<SpanSet> spanSets) {
        this.spanSets = spanSets;
    }

    @JsonGetter("serviceStats")
    public Map<String, Object> getServiceStats() {
        return serviceStats;
    }

    public void setServiceStats(Map<String, Object> serviceStats) {
        this.serviceStats = serviceStats;
    }
}
