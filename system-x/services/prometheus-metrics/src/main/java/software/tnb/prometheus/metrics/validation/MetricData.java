package software.tnb.prometheus.metrics.validation;

import java.util.List;
import java.util.Map;

/**
 * Metric as a vector with datapoints and related time info and generic metadata related to the metric itself
 */
public class MetricData {

    private Map<String, String> metaData;

    private List<InstantValue> values;

    public MetricData(Map<String, String> metaData, List<InstantValue> values) {
        super();
        this.metaData = metaData;
        this.values = values;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public List<InstantValue> getValues() {
        return values;
    }
}
