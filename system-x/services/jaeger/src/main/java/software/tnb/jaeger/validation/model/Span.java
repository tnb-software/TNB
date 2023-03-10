package software.tnb.jaeger.validation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Span {

    private String traceID;

    private String spanID;

    private String operationName;

    private List<KTVItem> tags;

    private List<KTVItem> logs;

    public String getTraceID() {
        return traceID;
    }

    public String getSpanID() {
        return spanID;
    }

    public String getOperationName() {
        return operationName;
    }

    public List<KTVItem> getTags() {
        return tags;
    }

    public List<KTVItem> getLogs() {
        return logs;
    }

    @JsonIgnore
    private String serviceName;

    public Span withServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Span span = (Span) o;
        return traceID.equals(span.traceID) && spanID.equals(span.spanID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traceID, spanID);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Span.class.getSimpleName() + "[", "]")
            .add("traceID='" + traceID + "'")
            .add("spanID='" + spanID + "'")
            .toString();
    }
}
