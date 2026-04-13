import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.apache.sshd.server.shell.ProcessShellCommandFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class MinaSshdServer {

    private MinaSshdServer() {
    }

    private static final Path AUTHORIZED_KEYS = Paths.get("/home/test/.ssh/authorized_keys");
    private static final Path HOST_KEY = Paths.get("/opt/mina-sshd/host.key");

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("MINA_SSH_PORT", "22"));

        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(HOST_KEY));

        // Password auth: accept if username == password
        sshd.setPasswordAuthenticator((username, password, session) -> username.equals(password));

        // Public key auth: verify against authorized_keys if present, otherwise accept all
        if (Files.exists(AUTHORIZED_KEYS)) {
            System.err.println("Public key authentication enabled from " + AUTHORIZED_KEYS);
            sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(AUTHORIZED_KEYS));
        } else {
            System.err.println("No authorized_keys found, accepting all public keys");
            sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
        }

        sshd.setShellFactory(InteractiveProcessShellFactory.INSTANCE);
        sshd.setCommandFactory(ProcessShellCommandFactory.INSTANCE);

        sshd.start();
        System.err.println("Starting SSHD on port " + port);

        Thread.currentThread().join();
    }
}
