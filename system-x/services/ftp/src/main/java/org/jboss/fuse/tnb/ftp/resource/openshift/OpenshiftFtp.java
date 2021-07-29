package org.jboss.fuse.tnb.ftp.resource.openshift;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.common.deployment.WithName;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.ftp.service.CustomFtpClient;
import org.jboss.fuse.tnb.ftp.service.Ftp;

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

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
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

@AutoService(Ftp.class)
public class OpenshiftFtp extends Ftp implements OpenshiftDeployable, WithName {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftFtp.class);

    private final CustomFtpClient client = new OpenShiftFtpClient();

    public static final int FTP_COMMAND_PORT = 2121;
    public static final int FTP_DATA_PORT_START = 2122;
    public static final int FTP_DATA_PORT_END = 2130;

    @Override
    public void create() {

        List<ContainerPort> ports = new LinkedList<>();
        ports.add(new ContainerPortBuilder()
            .withName("ftp-cmd")
            .withContainerPort(port())
            .withProtocol("TCP").build());

        for (int dataPort = FTP_DATA_PORT_START; dataPort <= FTP_DATA_PORT_END; dataPort++) {
            ContainerPort containerPort = new ContainerPortBuilder()
                .withName("ftp-data-" + dataPort)
                .withContainerPort(dataPort)
                .withProtocol("TCP")
                .build();
            ports.add(containerPort);
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
                .withName(name()).withImage(ftpImage()).addAllToPorts(ports)
                .withEnv(new EnvVar("USERS", containerEnvironment().get("USERS"), null))
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build()
        );

        ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name());

        serviceSpecBuilder.addToPorts(new ServicePortBuilder()
            .withName("ftp-cmd")
            .withPort(port())
            .withTargetPort(new IntOrString(port()))
            .build());

        for (int dataPort = FTP_DATA_PORT_START; dataPort <= FTP_DATA_PORT_END; dataPort++) {
            serviceSpecBuilder.addToPorts(new ServicePortBuilder()
                .withName("ftp-data-" + dataPort)
                .withPort(dataPort)
                .withTargetPort(new IntOrString(dataPort))
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
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .timeout(120_000).waitFor();
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
        return ResourceFunctions.areExactlyNPodsReady(1)
            .apply(OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()))
            && OpenshiftClient.get().getLogs(OpenshiftClient.get().getAnyPod(OpenshiftConfiguration.openshiftDeploymentLabel(), name()))
            .contains("FtpServer started");
    }

    @Override
    public boolean isDeployed() {
        Deployment deployment = OpenshiftClient.get().apps().deployments().withName(name()).get();
        return
            deployment != null
            && !deployment.isMarkedForDeletion()
            && isReady();
    }

    @Override
    public String name() {
        return "ftp";
    }

    @Override
    protected CustomFtpClient client() {
        return client;
    }

    @Override
    public int port() {
        return FTP_COMMAND_PORT;
    }

    @Override
    public String host() {
        return name();
    }

    public class OpenShiftFtpClient implements CustomFtpClient {

        @Override
        public void storeFile(String fileName, InputStream fileContent) throws IOException {
            Path tempFile = Files.createTempFile(null, null);
            try {
                Files.copy(fileContent, tempFile, StandardCopyOption.REPLACE_EXISTING);
                getPodResource().file("/tmp/" + account().username() + "/" + fileName).upload(tempFile);
            } finally {
                tempFile.toFile().delete();
            }
        }

        @Override
        public void retrieveFile(String fileName, OutputStream local) throws IOException {
            Path tempFile = Files.createTempFile(null, null);
            try {
                getPodResource().file("/tmp/" + account().username() + "/" + fileName).copy(tempFile);
                org.apache.commons.io.IOUtils.copy(Files.newInputStream(tempFile), local);
            } finally {
                tempFile.toFile().delete();
            }
        }

        @Override
        public void makeDirectory(String dirName) {
            getPodResource().writingOutput(new ByteArrayOutputStream())
                .exec("mkdir", "-p", "-m", "a=rwx", String.format("%s/%s", basePath(), dirName));
        }

        private PodResource<Pod> getPodResource() {
            Pod ftpPod = OpenshiftClient.get().getAnyPod(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
            return OpenshiftClient.get().pods().withName(ftpPod.getMetadata().getName());
        }
    }
}
