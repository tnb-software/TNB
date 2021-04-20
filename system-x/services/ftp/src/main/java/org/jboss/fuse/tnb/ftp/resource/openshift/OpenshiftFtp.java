package org.jboss.fuse.tnb.ftp.resource.openshift;

import io.fabric8.kubernetes.api.model.EnvVar;

import io.fabric8.kubernetes.api.model.Pod;

import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;

import org.apache.commons.net.ftp.FTP;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.SystemXConfiguration;
import org.jboss.fuse.tnb.common.deployment.OpenshiftNamedDeployable;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.ftp.service.Ftp;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.client.PortForward;

@AutoService(Ftp.class)
public class OpenshiftFtp extends Ftp implements OpenshiftNamedDeployable {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftFtp.class);

    private FTPClient client;

    private List<PortForward> portForwards = new ArrayList<>();

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
                .withName(name()).withImage(SystemXConfiguration.ftpImage()).addAllToPorts(ports)
                .withEnv(new EnvVar("FTP_USERNAME", account().username(), null), new EnvVar("FTP_PASSWORD", account().password(), null))
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
        try {
            client().disconnect();
        } catch (IOException ignored) {
        }
        for (PortForward portForward : portForwards) {
            IOUtils.closeQuietly(portForward);
        }

        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areExactlyNPodsReady(0, OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .timeout(120_000).waitFor();
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1)
            .apply(OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()))
            && OpenshiftClient.getLogs(OpenshiftClient.get().getAnyPod(OpenshiftConfiguration.openshiftDeploymentLabel(), name()))
            .contains("event=Starting");
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).size() != 0;
    }

    @Override
    public String name() {
        return "ftp";
    }

    @Override
    public FTPClient client() {
        if (client == null) {
            setupPortForwards();
            WaitUtils.sleep(1000);
            makeClient();
        }
        return client;
    }

    private void makeClient() {
        try {
            LOG.debug("Creating new FTPClient instance");
            client = new OpenShiftFtpClient();
            client.connect(localClientHost(), port());
            client.login(account().username(), account().password());
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.setDataTimeout(1000);
            client.setRemoteVerificationEnabled(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int port() {
        return FTP_COMMAND_PORT;
    }

    @Override
    protected String localClientHost() {
        return "localhost";
    }

    @Override
    public String host() {
        return name();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        undeploy();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        deploy();
    }

    private void setupPortForwards() {
        Pod ftpPod = OpenshiftClient.get().getAnyPod(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
        for (int dataPort = FTP_DATA_PORT_START; dataPort <= FTP_DATA_PORT_END; dataPort++) {
            portForwards.add(OpenshiftClient.get().portForward(ftpPod, dataPort, dataPort));
        }
        PortForward commandPortForward = OpenshiftClient.get().services().withName(name()).portForward(port(), port());
        portForwards.add(commandPortForward);
    }

    /**
     * Custom client to work around FTP issues in openshift
     */
    public class OpenShiftFtpClient extends FTPClient {

        @Override
        public boolean storeFile(String fileName, InputStream fileContent) throws IOException {
            // transferring files over FTP using fabric8 port-forward is extremely unreliable, copy the file directly into the container instead
            Path tempFile = Files.createTempFile(null, null);
            try {
                Files.copy(fileContent, tempFile, StandardCopyOption.REPLACE_EXISTING);
                Pod ftpPod = OpenshiftClient.get().getAnyPod(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
                OpenshiftClient.get().pods().withName(ftpPod.getMetadata().getName())
                    .file("/tmp/" + fileName).upload(tempFile);
            } finally {
                tempFile.toFile().delete();
            }
            return true;
        }
    }
}
