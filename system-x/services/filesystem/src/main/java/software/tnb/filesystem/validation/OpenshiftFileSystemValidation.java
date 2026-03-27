package software.tnb.filesystem.validation;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import io.fabric8.kubernetes.api.model.Pod;

public class OpenshiftFileSystemValidation extends FileSystemValidation {
    private final Pod pod;
    private final String container;

    public OpenshiftFileSystemValidation(String name) {
        pod = OpenshiftClient.get().getAnyPod("app.kubernetes.io/name", name);
        container = OpenshiftClient.get().getIntegrationContainer(pod);
    }

    @Override
    public void createFile(Path file, String content) {
        createFile(file, content.getBytes());
    }

    @Override
    public void createFile(Path file, byte[] content) {
        OpenshiftClient.get().pods()
            .withName(pod.getMetadata().getName())
            .inContainer(container)
            .file(file.toAbsolutePath().toString())
            .upload(new ByteArrayInputStream(content));
    }

    @Override
    public void deleteFile(Path file) {
        OpenshiftClient.get().podShell(pod, container).execute("rm", "-f", file.toAbsolutePath().toString());
    }

    @Override
    public byte[] getFile(Path file) {
        try (InputStream is = OpenshiftClient.get().pods()
            .inNamespace(OpenshiftClient.get().getNamespace())
            .withName(pod.getMetadata().getName())
            .inContainer(container).file(file.toString()).read()) {
            // is.readAllBytes() does not work well for large files, so read the file manually with buffers
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int n;
            while ((n = is.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file", e);
        }
    }

    @Override
    public void copyFile(Path srcPath, Path destPath) {
        if (srcPath.toFile().exists() && !exists(destPath)) {
            OpenshiftClient.get().pods()
                .withName(pod.getMetadata().getName())
                .inContainer(container)
                .file(destPath.toString())
                .upload(srcPath);
        } else if (!destPath.toFile().exists() && exists(srcPath)) {
            IOUtils.writeFile(destPath, getFile(srcPath));
        } else {
            throw new IllegalArgumentException("Both or neither file exist on OCP or locally");
        }
    }

    @Override
    public Path createDirectory(Path directory) {
        OpenshiftClient.get().podShell(pod, container).execute("mkdir", "-p", directory.toAbsolutePath().toString());
        return directory;
    }

    @Override
    public void deleteDirectory(Path directory) {
        OpenshiftClient.get().podShell(pod, container).execute("rm", "-rf", directory.toAbsolutePath().toString());
    }

    @Override
    public boolean exists(Path path) {
        return Integer.parseInt(OpenshiftClient.get().podShell(pod, container)
            .executeWithBash("test -f " + path.toAbsolutePath() + "; echo $?").getOutput().trim()) == 0;
    }
}
