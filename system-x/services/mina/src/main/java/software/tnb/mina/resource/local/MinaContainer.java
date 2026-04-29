package software.tnb.mina.resource.local;

import software.tnb.mina.service.Mina;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MinaContainer extends GenericContainer<MinaContainer> {

    private static final String AUTHORIZED_KEYS_PATH = "/home/test/.ssh/authorized_keys";

    public MinaContainer(String image, Path publicKeyPath, Path caPublicKeyPath) {
        super(image);
        withExposedPorts(Mina.SSHD_LISTENING_PORT);
        waitingFor(Wait.forLogMessage(".*Starting SSHD on port.*", 1));

        if (caPublicKeyPath != null) {
            withCopyToContainer(
                Transferable.of(buildAuthorizedKeysContent(publicKeyPath, caPublicKeyPath).getBytes(StandardCharsets.UTF_8)),
                AUTHORIZED_KEYS_PATH
            );
        } else if (publicKeyPath != null) {
            withCopyFileToContainer(MountableFile.forHostPath(publicKeyPath), AUTHORIZED_KEYS_PATH);
        }
    }

    private static String buildAuthorizedKeysContent(Path publicKeyPath, Path caPublicKeyPath) {
        StringBuilder sb = new StringBuilder();
        try {
            if (publicKeyPath != null) {
                sb.append(Files.readString(publicKeyPath).trim()).append("\n");
            }
            sb.append("cert-authority ").append(Files.readString(caPublicKeyPath).trim()).append("\n");
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read SSH key files", e);
        }
        return sb.toString();
    }
}
