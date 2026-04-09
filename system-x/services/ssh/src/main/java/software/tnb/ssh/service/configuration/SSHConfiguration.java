package software.tnb.ssh.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

import java.nio.file.Path;
import java.util.Optional;

public class SSHConfiguration extends ServiceConfiguration {
    private static final String PUBLIC_KEY_PATH = "ssh.public-key-path";
    private static final String PRIVATE_KEY_PATH = "ssh.private-key-path";

    public SSHConfiguration withPublicKeyPath(Path value) {
        set(PUBLIC_KEY_PATH, value.toAbsolutePath().toString());
        return this;
    }

    public SSHConfiguration withPrivateKeyPath(Path value) {
        set(PRIVATE_KEY_PATH, value.toAbsolutePath().toString());
        return this;
    }

    public Path publicKeyPath() {
        return Optional.ofNullable(get(PUBLIC_KEY_PATH, String.class))
            .map(Path::of)
            .orElse(null);
    }

    public Path privateKeyPath() {
        return Optional.ofNullable(get(PRIVATE_KEY_PATH, String.class))
            .map(Path::of)
            .orElse(null);
    }
}
