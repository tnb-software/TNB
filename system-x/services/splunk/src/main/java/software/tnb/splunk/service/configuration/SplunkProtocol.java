package software.tnb.splunk.service.configuration;

public enum SplunkProtocol {
    HTTP("http"),
    HTTPS("https");

    private final String value;

    SplunkProtocol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
