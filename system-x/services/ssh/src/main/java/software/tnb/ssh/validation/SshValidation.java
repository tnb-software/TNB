package software.tnb.ssh.validation;

import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.security.Security;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;
import net.schmizz.sshj.userauth.method.AuthPublickey;

public class SshValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(SshValidation.class);

    private final String username;
    private final String host;
    private final int port;

    public SshValidation(String username, String host, int port) {
        this.username = username;
        this.host = host;
        this.port = port;
    }

    public SSHClient getSSHClient(String privateKey) throws IOException {
        SSHClient ssh = new SSHClient();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.connect(host, port);

        PKCS8KeyFile keyFile = new PKCS8KeyFile();
        keyFile.init(Path.of(privateKey).toFile());
        ssh.auth(this.username, new AuthPublickey(keyFile));
        return ssh;
    }

    public String sendCommand(String command, String privateKey) throws Exception {
        SSHClient ssh = getSSHClient(privateKey);
        Session session = null;
        String response = "no response";
        try {
            session = ssh.startSession();
            final Command cmd = session.exec(command);
            response = (IOUtils.readFully(cmd.getInputStream()).toString());
            cmd.join(5, TimeUnit.SECONDS);
            LOG.info("\n** exit status: {}", cmd.getExitStatus());
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (IOException e) {
                LOG.error("Excpetion during command exec:", e);
            }

            ssh.disconnect();
        }
        return response;
    }

}
