package software.tnb.mina.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

import java.nio.file.Path;
import java.util.Optional;

public class MinaConfiguration extends ServiceConfiguration {
    private static final String PRIVATE_KEY_PATH = "mina.private-key-path";
    private static final String PUBLIC_KEY_PATH = "mina.public-key-path";

    public MinaConfiguration withPrivateKeyPath(Path value) {
        set(PRIVATE_KEY_PATH, value.toAbsolutePath().toString());
        return this;
    }

    public Path privateKeyPath() {
        return Optional.ofNullable(get(PRIVATE_KEY_PATH, String.class))
            .map(Path::of)
            .orElse(null);
    }

    public MinaConfiguration withPublicKeyPath(Path value) {
        set(PUBLIC_KEY_PATH, value.toAbsolutePath().toString());
        return this;
    }

    public Path publicKeyPath() {
        return Optional.ofNullable(get(PUBLIC_KEY_PATH, String.class))
            .map(Path::of)
            .orElse(null);
    }
}
