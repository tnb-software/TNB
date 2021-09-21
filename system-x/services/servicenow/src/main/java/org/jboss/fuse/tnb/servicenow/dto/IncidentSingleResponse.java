package org.jboss.fuse.tnb.servicenow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing response from api when there is just one incident object.
 */
public class IncidentSingleResponse {
    @JsonProperty("result")
    private Incident record;

    public IncidentSingleResponse() {
    }

    public Incident getRecord() {
        return this.record;
    }

    public void setRecord(Incident record) {
        this.record = record;
    }

    public String toString() {
        return "IncidentSingleResponse(record=" + this.getRecord() + ")";
    }
}
