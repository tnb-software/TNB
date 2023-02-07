package software.tnb.splunk.service.configuration;

public enum SplunkProtocol {
    HTTP,
    HTTPS;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
