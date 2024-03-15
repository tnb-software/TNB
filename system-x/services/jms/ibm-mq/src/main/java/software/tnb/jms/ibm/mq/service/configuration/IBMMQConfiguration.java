package software.tnb.jms.ibm.mq.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

import java.nio.file.Path;
import java.util.Optional;

public class IBMMQConfiguration extends ServiceConfiguration {

    private static final String KEY_PATH = "KEY_PATH";
    private static final String CERT_PATH = "CERT_PATH";
    private static final String USE_SSL = "USE_SSL";

    public IBMMQConfiguration withKeyPath(Path value) {
        set(KEY_PATH, value.toAbsolutePath().toString());
        return this;
    }

    public IBMMQConfiguration withCertPath(Path value) {
        set(CERT_PATH, value.toAbsolutePath().toString());
        return this;
    }

    public IBMMQConfiguration useSSL(boolean value) {
        set(USE_SSL, value);
        return this;
    }

    public boolean useSSL() {
        return Optional.ofNullable(get(USE_SSL, Boolean.class)).orElse(Boolean.FALSE);
    }

    public Path certPath() {
        return Optional.ofNullable(get(CERT_PATH, String.class)).map(Path::of).orElse(null);
    }

    public Path keyPath() {
        return Optional.ofNullable(get(KEY_PATH, String.class)).map(Path::of).orElse(null);
    }
}
