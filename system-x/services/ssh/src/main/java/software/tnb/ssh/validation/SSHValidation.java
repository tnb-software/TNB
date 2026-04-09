package software.tnb.ssh.validation;

import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;

public class SSHValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(SSHValidation.class);

    private final SSHClient sshClient;

    public SSHValidation(SSHClient sshClient) {
        this.sshClient = sshClient;
    }

    public String sendCommand(String command) {
        Session session = null;
        String response;
        try {
            session = sshClient.startSession();
            final Command cmd = session.exec(command);
            response = (IOUtils.readFully(cmd.getInputStream()).toString());
            cmd.join(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute command", e);
        } finally {
            if (session != null) {
                org.apache.commons.io.IOUtils.closeQuietly(session);
            }
        }
        return response;
    }

}
