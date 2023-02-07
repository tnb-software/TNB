package software.tnb.splunk.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class SplunkConfiguration extends ServiceConfiguration {

    private static final String SPLUNK_PROTOCOL = "splunk.protocol";

    public SplunkConfiguration protocol(SplunkProtocol splunkProtocol) {
        set(SPLUNK_PROTOCOL, splunkProtocol);
        return this;
    }

    public SplunkProtocol getProtocol() {
        return get(SPLUNK_PROTOCOL, SplunkProtocol.class);
    }
}
