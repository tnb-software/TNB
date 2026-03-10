package software.tnb.ftp.ftp.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class FTPConfiguration extends ServiceConfiguration {
    private static final String IDLE_TIMEOUT = "ftp.idle.timeout";

    public FTPConfiguration idleTimeout(int seconds) {
        set(IDLE_TIMEOUT, seconds);
        return this;
    }

    public int getIdleTimeout() {
        return get(IDLE_TIMEOUT, Integer.class);
    }
}
