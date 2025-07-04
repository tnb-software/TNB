package software.tnb.observability.validation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Span {
    String traceId;
    String spanId;
    String parentSpanId;
    int flags;
    String name;
    String kind;
    String startTimeUnixNano;
    String endTimeUnixNano;
    ArrayList<Attribute> attributes;
    ArrayList<Event> events;
    Status status;

    @JsonProperty("traceId")
    public String getTraceId() {
        return this.traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @JsonProperty("spanId")
    public String getSpanId() {
        return this.spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    @JsonProperty("parentSpanId")
    public String getParentSpanId() {
        return this.parentSpanId;
    }

    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    @JsonProperty("flags")
    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("kind")
    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @JsonProperty("startTimeUnixNano")
    public String getStartTimeUnixNano() {
        return this.startTimeUnixNano;
    }

    public void setStartTimeUnixNano(String startTimeUnixNano) {
        this.startTimeUnixNano = startTimeUnixNano;
    }

    @JsonProperty("endTimeUnixNano")
    public String getEndTimeUnixNano() {
        return this.endTimeUnixNano;
    }

    public void setEndTimeUnixNano(String endTimeUnixNano) {
        this.endTimeUnixNano = endTimeUnixNano;
    }

    @JsonProperty("attributes")
    public ArrayList<Attribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("events")
    public ArrayList<Event> getEvents() {
        return this.events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
