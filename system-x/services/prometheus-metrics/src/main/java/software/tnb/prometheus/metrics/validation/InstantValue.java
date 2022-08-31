package software.tnb.prometheus.metrics.validation;

/**
 * A datapoint with the related time field
 */
public class InstantValue {

    private Number value;
    private long instant;

    public InstantValue(Number value, long instant) {
        this.value = value;
        this.instant = instant;
    }

    public Number getValue() {
        return value;
    }

    public long getInstant() {
        return instant;
    }
}
