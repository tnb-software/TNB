package software.tnb.jms.amq.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class AMQConfiguration extends ServiceConfiguration {
    private static final String AMQ_REQUIRE_LOGIN = "amq.require.login";

    public AMQConfiguration requireLogin(boolean requireLogin) {
        set(AMQ_REQUIRE_LOGIN, requireLogin);
        return this;
    }

    public boolean isRequireLogin() {
        return get(AMQ_REQUIRE_LOGIN, Boolean.class);
    }
}
