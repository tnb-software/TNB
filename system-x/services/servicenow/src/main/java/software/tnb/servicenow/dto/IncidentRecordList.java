package software.tnb.servicenow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing response from api when there are multiple incident objects.
 */
public class IncidentRecordList {
    @JsonProperty("result")
    private List<Incident> records;

    public List<Incident> getRecords() {
        if (records == null) {
            records = new ArrayList<>();
        }
        return this.records;
    }
}
