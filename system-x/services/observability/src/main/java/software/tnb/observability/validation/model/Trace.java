package software.tnb.observability.validation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Trace {
    ArrayList<Batch> batches;

    @JsonProperty("batches")
    public ArrayList<Batch> getBatches() {
        return this.batches;
    }

    public void setBatches(ArrayList<Batch> batches) {
        this.batches = batches;
    }
}
