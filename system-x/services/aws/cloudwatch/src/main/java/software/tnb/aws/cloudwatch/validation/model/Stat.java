package software.tnb.aws.cloudwatch.validation.model;

public enum Stat {
    AVERAGE("Average"),
    MINIMUM("Minimum"),
    MAXIMUM("Maximum"),
    SUM("Sum"),
    SAMPLE_COUNT("SampleCount"),
    IQM("IQM");
    // Others like percentile, trimmed mean, etc. require a value, so those can be supplied via a plain string for now

    private final String value;

    Stat(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
