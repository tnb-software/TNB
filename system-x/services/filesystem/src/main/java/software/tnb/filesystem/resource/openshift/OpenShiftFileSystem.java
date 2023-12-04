package software.tnb.filesystem.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.filesystem.service.FileSystem;

import com.google.auto.service.AutoService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.OpenShiftWaiters;
import io.fabric8.kubernetes.api.model.Pod;

@AutoService(FileSystem.class)
public class OpenShiftFileSystem extends FileSystem implements OpenshiftDeployable {
    private static final String NAMESPACE = OpenshiftClient.get().getNamespace();
    private String podLabelValue;

    @Override
    public void setAppName(String app) {
        this.podLabelValue = app;
    }

    @Override
    public String getFileContent(Path path) {
        final String podLabelKey = "deploymentconfig";
        podIsReady(podLabelKey, podLabelValue);
        final String podName = getPodName(podLabelKey, podLabelValue);
        final Pod pod = OpenshiftClient.get().getPod(podName);
        final String integrationContainer = OpenshiftClient.get().getIntegrationContainer(pod);

        try (InputStream is = OpenshiftClient.get().pods()
            .inNamespace(NAMESPACE)
            .withName(podName)
            .inContainer(integrationContainer).file(path.toString()).read()) {
            return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}
