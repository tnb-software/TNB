package software.tnb.splunk.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class SplunkConfiguration extends ServiceConfiguration {

    private static final String SPLUNK_PROTOCOL = "splunk.protocol";
    private static final String SPLUNK_HEC_ENABLED = "splunk.hec.enabled";

    public SplunkConfiguration protocol(SplunkProtocol splunkProtocol) {
        set(SPLUNK_PROTOCOL, splunkProtocol);
        return this;
    }

    public SplunkProtocol getProtocol() {
        return get(SPLUNK_PROTOCOL, SplunkProtocol.class);
    }

    public SplunkConfiguration hecEnabled(boolean splunkHecEnabled) {
        set(SPLUNK_HEC_ENABLED, splunkHecEnabled);
        return this;
    }

    public boolean isHecEnabled() {
        return get(SPLUNK_HEC_ENABLED, Boolean.class);
    }
}
