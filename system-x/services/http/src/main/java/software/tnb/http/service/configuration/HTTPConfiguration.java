package software.tnb.http.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class HTTPConfiguration extends ServiceConfiguration {
    private static final String HTTP_NAME = "http.name";

    public HTTPConfiguration name(String name) {
        set(HTTP_NAME, name);
        return this;
    }

    public String getName() {
        return get(HTTP_NAME, String.class);
    }
}
