package software.tnb.ftp.ftp.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.ftp.ftp.service.CustomFTPClient;
import software.tnb.ftp.ftp.service.FTP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.dsl.PodResource;

@AutoService(FTP.class)
public class OpenshiftFTP extends FTP implements OpenshiftDeployable, WithName, WithInClusterHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftFTP.class);

    private final CustomFTPClient client = new OpenShiftFTPClient();

    @Override
    public void create() {

        List<ContainerPort> ports = new LinkedList<>();
        List<Integer> ftpPorts = containerPorts();
        for (int i = 0; i < ftpPorts.size(); i++) {
            ports.add(new ContainerPortBuilder()
                .withName("ftp-" + i)
                .withContainerPort(ftpPorts.get(i))
                .withProtocol("TCP").build());
        }

        OpenshiftClient.get().apps().deployments().createOrReplace(
            new DeploymentBuilder()
                .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpec()
                .editOrNewSelector()
                .addToMatchLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endSelector()
                .withReplicas(1)
                .editOrNewTemplate()
                .editOrNewMetadata()
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpec()
                .addNewContainer()
                .withName(name()).withImage(image()).addAllToPorts(ports)
                .withEnv(new EnvVar("USERS", containerEnvironment().get("USERS"), null))
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build()
        );

        ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name());

        for (int i = 0; i < ftpPorts.size(); i++) {
            serviceSpecBuilder.addToPorts(new ServicePortBuilder()
                .withName("ftp-" + i)
                .withPort(ftpPorts.get(i))
                .withTargetPort(new IntOrString(ftpPorts.get(i)))
                .build());
        }

        OpenshiftClient.get().services().createOrReplace(
            new ServiceBuilder()
                .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpecLike(serviceSpecBuilder.build())
                .endSpec()
                .build()
        );
    }

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift ftp");
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        // noop
    }

    @Override
    public void closeResources() {
        // noop
    }

    @Override
    public boolean isReady() {
        return Optional.ofNullable(servicePod())
            .filter(PodResource::isReady)
            .map(pod -> OpenshiftClient.get().getLogs(pod.get()).contains("FtpServer started"))
            .orElse(false);
    }

    @Override
    public boolean isDeployed() {
        Deployment deployment = OpenshiftClient.get().apps().deployments().withName(name()).get();
        return deployment != null && !deployment.isMarkedForDeletion();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public String name() {
        return "ftp";
    }

    @Override
    protected CustomFTPClient client() {
        return client;
    }

    @Override
    public String logs() {
        return OpenshiftClient.get().getLogs(servicePod().get());
    }

    @Override
    public String host() {
        return inClusterHostname();
    }

    @Override
    public String hostForActiveConnection() {
        return servicePod().get().getStatus().getPodIP();
    }

    public class OpenShiftFTPClient implements CustomFTPClient {

        @Override
        public void storeFile(String fileName, InputStream fileContent) throws IOException {
            Path tempFile = Files.createTempFile(null, null);
            try {
                Files.copy(fileContent, tempFile, StandardCopyOption.REPLACE_EXISTING);
                servicePod().file("/tmp/" + account().username() + "/" + fileName).upload(tempFile);
            } finally {
                tempFile.toFile().delete();
            }
        }

        @Override
        public void retrieveFile(String fileName, OutputStream local) throws IOException {
            Path tempFile = Files.createTempFile(null, null);
            try {
                servicePod().file("/tmp/" + account().username() + "/" + fileName).copy(tempFile);
                org.apache.commons.io.IOUtils.copy(Files.newInputStream(tempFile), local);
            } finally {
                tempFile.toFile().delete();
            }
        }

        @Override
        public void makeDirectory(String dirName) {
            servicePod().writingOutput(new ByteArrayOutputStream())
                .exec("mkdir", "-p", "-m", "a=rwx", String.format("%s/%s", basePath(), dirName));
        }

        @Override
        public List<String> listFolder(String dirName) {
            try {
                return List.of(new String(servicePod().redirectingOutput()
                    .exec("/bin/bash", "-c", String.format("ls -p %s/%s | grep -v /", basePath(), dirName)).getOutput().readAllBytes())
                    .split("\n"));
            } catch (IOException e) {
                throw new RuntimeException("Unable to read command output: " + e);
            }
        }
    }
}
