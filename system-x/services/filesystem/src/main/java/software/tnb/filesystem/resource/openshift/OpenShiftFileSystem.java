package software.tnb.filesystem.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.filesystem.service.FileSystem;

import org.junit.jupiter.api.Assertions;

import org.awaitility.Awaitility;

import com.google.auto.service.AutoService;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.OpenShiftWaiters;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.ExecWatch;

@AutoService(FileSystem.class)
public class OpenShiftFileSystem extends FileSystem implements OpenshiftDeployable {
    private String podLabelValue;
    private final String directory = "/tmp/tnb-ocp-filesystem";

    @Override
    public void setAppName(String app) {
        this.podLabelValue = app;
    }

    @Override
    public String getFileContent(Path path) {
        final String podLabelKey = "app.kubernetes.io/name";
        podIsReady(podLabelKey, podLabelValue);
        final String podName = getPodName(podLabelKey, podLabelValue);
        final Pod pod = OpenshiftClient.get().getPod(podName);
        final String integrationContainer = OpenshiftClient.get().getIntegrationContainer(pod);

        try (InputStream is = OpenshiftClient.get().pods()
            .inNamespace(OpenshiftClient.get().getNamespace())
            .withName(podName)
            .inContainer(integrationContainer).file(path.toString()).read()) {
            return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Transfers a file between the local filesystem and a pod. 
     * Works both ways, meaning it will download a file from a pod if the source is remote
     * and upload a file to a pod if the source is local.
     * One path must be remote and one path must be local.
     *
     * @param srcPath the source file.
     * @param destPath the destination. This file should not exist.
     */
    @Override
    public void copyFile(Path srcPath, Path destPath) throws IOException {
        final String podLabelKey = "app.kubernetes.io/name";
        podIsReady(podLabelKey, podLabelValue);
        final String podName = getPodName(podLabelKey, podLabelValue);
        final Pod pod = OpenshiftClient.get().getPod(podName);
        final String integrationContainer = OpenshiftClient.get().getIntegrationContainer(pod);
        
        if (srcPath.toFile().exists() && !fileExistsOnOCP(destPath.toString())) {
            OpenshiftClient.get().pods()
                .inNamespace(OpenshiftClient.get().getNamespace())
                .withName(podName)
                .inContainer(integrationContainer).file(destPath.toString())
                .upload(srcPath);
            waitForFile(destPath.toString());
        } else if (!destPath.toFile().exists() && fileExistsOnOCP(srcPath.toString())) {
            try (InputStream is = OpenshiftClient.get().pods()
                .inNamespace(OpenshiftClient.get().getNamespace())
                .withName(podName)
                .inContainer(integrationContainer).file(srcPath.toString()).read()) {
                    Files.write(
                        destPath, 
                        new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining(System.lineSeparator())).getBytes()
                    );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IOException("Both or neither file exist on OCP or locally");
        }
    }

    @Override
    public boolean createFile(Path directory, String filename, String content) throws IOException {
        final String podLabelKey = "app.kubernetes.io/name";
        podIsReady(podLabelKey, podLabelValue);
        final String podName = getPodName(podLabelKey, podLabelValue);
        final Pod pod = OpenshiftClient.get().getPod(podName);
        final String integrationContainer = OpenshiftClient.get().getIntegrationContainer(pod);

        return OpenshiftClient.get().pods()
            .inNamespace(OpenshiftClient.get().getNamespace())
            .withName(podName)
            .inContainer(integrationContainer).file(Path.of(directory.toString(), filename).toString())
            .upload(new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public Path createTempDirectory() throws IOException {
        // use deployment/*
        return Path.of(directory);
    }

    @Override
    public void create() {
    }

    @Override
    public boolean isDeployed() {
        return true;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return null;
    }

    private String getPodName(String key, String value) {
        return OpenshiftClient.get().getLabeledPods(key, value).get(0).getMetadata().getName();
    }

    private void podIsReady(String key, String value) {
        try {
            OpenShiftWaiters.get(OpenshiftClient.get(), () -> false)
                .areExactlyNPodsReady(1, key, value).interval(TimeUnit.SECONDS, 10).timeout(TimeUnit.MINUTES, 10).waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean fileExistsOnOCP(String path) throws IOException {
        final String podLabelKey = "app.kubernetes.io/name";
        final String podName = getPodName(podLabelKey, podLabelValue);
        final Pod pod = OpenshiftClient.get().getPod(podName);
        final String integrationContainer = OpenshiftClient.get().getIntegrationContainer(pod);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                ExecWatch watch = OpenshiftClient.get().pods()
                    .inNamespace(OpenshiftClient.get().getNamespace())
                    .withName(podName)
                    .inContainer(integrationContainer)
                    .writingOutput(out)
                    .exec("test", "-f", path)) {
                    return watch.exitCode().join().equals(0);
                }
    }

    private void waitForFile(String path) {
        Awaitility.await("Wait for " + path + "to be uploaded")
            .atMost(30, TimeUnit.MINUTES)
            .pollInterval(20, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Assertions.assertTrue(fileExistsOnOCP(path));
            });
    }
}
