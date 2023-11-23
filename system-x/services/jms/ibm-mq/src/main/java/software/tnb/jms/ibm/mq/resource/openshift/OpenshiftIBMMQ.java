package software.tnb.jms.ibm.mq.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.jms.ibm.mq.service.IBMMQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KeyToPathBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePortBuilder;
import io.fabric8.openshift.api.model.RouteTargetReferenceBuilder;
import io.fabric8.openshift.api.model.TLSConfigBuilder;

@AutoService(IBMMQ.class)
public class OpenshiftIBMMQ extends IBMMQ implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftIBMMQ.class);
    // Theoretically not needed, but may be handy
    private static final int CONSOLE_PORT = 9443;
    private static final String CONFIG_MAP_NAME = "tnb-ibm-mq-config";
    private static final String CONFIG_MAP_VOLUME_NAME = "config";
    private PortForward portForward;
    private int localPort;
    private long uid;

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift IBM MQ");
        LOG.debug("Deleting services");
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        LOG.debug("Deleting routes");
        OpenshiftClient.get().routes().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        LOG.debug("Deleting deploymentconfig {}", name());
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        localPort = NetworkUtils.getFreePort();
        LOG.debug("Creating port-forward to {} for port {}", name(), DEFAULT_PORT);
        portForward = OpenshiftClient.get().services().withName(name()).portForward(DEFAULT_PORT, localPort);
        super.openResources();
    }

    @Override
    public void closeResources() {
        super.closeResources();
        if (portForward != null && portForward.isAlive()) {
            LOG.debug("Closing port-forward");
            IOUtils.closeQuietly(portForward);
        }
        NetworkUtils.releasePort(localPort);
    }

    @Override
    public void create() {
        LOG.info("Deploying OpenShift IBM MQ");

        /*
         * There is a hardcoded auth config for "mqm" user inside /etc/mqm/10-dev.mqsc and when running locally such user is created somewhere/somehow
         * in the ibm mq. However, when running on openshift, instead of "mqm" user there is a user with uid created and we don't know the uid
         * beforehand to change the configuration in the mentioned file.
         *
         * What this does it gets the uid interval from the namespace annotation and generates a random uid from that range and "hardcodes" that id
         * in the deployment config and changes the auth configuration
         */
        final List<Long> uidRange =
            Arrays.stream(OpenshiftClient.get().namespaces().withName(OpenshiftClient.get().getNamespace()).get().getMetadata()
                .getAnnotations().get("openshift.io/sa.scc.uid-range").split("/")).map(Long::parseLong).toList();
        uid = ThreadLocalRandom.current().nextLong(uidRange.get(0), uidRange.get(0) + uidRange.get(1));

        createMqscConfigMap();

        Map<String, Integer> ports = Map.of(name(), DEFAULT_PORT, name() + "-console", CONSOLE_PORT);
        List<ContainerPort> containerPorts = ports.entrySet().stream()
            .map(e -> new ContainerPortBuilder().withName(e.getKey()).withContainerPort(e.getValue()).withProtocol("TCP").build())
            .collect(Collectors.toList());

        // @formatter:off
        LOG.debug("Creating deploymentconfig {}", name());
        OpenshiftClient.get().deploymentConfigs().createOrReplace(
            new DeploymentConfigBuilder()
                .editOrNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .withReplicas(1)
                    .editOrNewTemplate()
                        .editOrNewMetadata()
                            .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                        .endMetadata()
                        .editOrNewSpec()
                            .addNewContainer()
                                .withName(name())
                                .withImage(image())
                                .addAllToPorts(containerPorts)
                                .addAllToEnv(containerEnvironment().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                                .collect(Collectors.toList()))
                                .addToVolumeMounts(new VolumeMountBuilder()
                                    .withName(CONFIG_MAP_VOLUME_NAME)
                                    .withMountPath(IBMMQ.MQSC_COMMAND_FILES_LOCATION + "/" + IBMMQ.MQSC_COMMAND_FILE_NAME)
                                    .withSubPath(IBMMQ.MQSC_COMMAND_FILE_NAME)
                                    .build()
                                )
                                .withNewSecurityContext()
                                    .withRunAsUser(uid)
                                .endSecurityContext()
                            .endContainer()
                        .withVolumes(new VolumeBuilder()
                            .withName(CONFIG_MAP_VOLUME_NAME)
                            .withConfigMap(new ConfigMapVolumeSourceBuilder()
                                .withName(CONFIG_MAP_NAME)
                                .withItems(new KeyToPathBuilder()
                                    .withKey(IBMMQ.MQSC_COMMAND_FILE_NAME)
                                    .withPath(IBMMQ.MQSC_COMMAND_FILE_NAME)
                                    .build()
                                )
                                .build()
                            )
                            .build()
                        )
                        .endSpec()
                    .endTemplate()
                    .addNewTrigger()
                        .withType("ConfigChange")
                    .endTrigger()
                .endSpec()
                .build()
        );

        ports.forEach((key, value) -> {
            ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name());

            serviceSpecBuilder.addToPorts(new ServicePortBuilder()
                .withName(key)
                .withPort(value)
                .withTargetPort(new IntOrString(value))
                .build());

            LOG.debug("Creating service {}", key);
            OpenshiftClient.get().services().createOrReplace(
                new ServiceBuilder()
                    .editOrNewMetadata()
                        .withName(key)
                        .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .endMetadata()
                    .editOrNewSpecLike(serviceSpecBuilder.build())
                    .endSpec()
                    .build()
            );
        });

        OpenshiftClient.get().routes().createOrReplace(new RouteBuilder()
            .withNewMetadata()
                .withName(name() + "-console")
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .withNewSpec()
                .withPort(new RoutePortBuilder().withNewTargetPort(CONSOLE_PORT).build())
                .withTls(new TLSConfigBuilder().withTermination("passthrough").build())
            .withTo(new RouteTargetReferenceBuilder().withKind("Service").withName(name() + "-console").build())
            .endSpec()
            .build()
        );

        // @formatter:on
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("Started web server");
    }

    @Override
    public boolean isDeployed() {
        return !OpenshiftClient.get().apps().deployments().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).list()
                .getItems().isEmpty();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public String hostname() {
        return inClusterHostname();
    }

    @Override
    public int port() {
        return DEFAULT_PORT;
    }

    @Override
    public int clientPort() {
        return localPort;
    }

    @Override
    public String name() {
        return "ibm-mq";
    }

    @Override
    public String mqscConfig() {
        // Change the hardcoded "mqm" user to the uid
        return super.mqscConfig() + "SET CHLAUTH('DEV.ADMIN.SVRCONN') TYPE(USERMAP) CLNTUSER('admin') USERSRC(MAP) MCAUSER ('" + uid
            + "') ACTION(REPLACE)\n";
    }

    private void createMqscConfigMap() {
        // @formatter:off
        LOG.debug("Creating configmap {}", CONFIG_MAP_NAME);
        OpenshiftClient.get().configMaps().createOrReplace(new ConfigMapBuilder()
            .withNewMetadata()
                .withName(CONFIG_MAP_NAME)
                .addToLabels(Map.of(OpenshiftConfiguration.openshiftDeploymentLabel(), name()))
            .endMetadata()
            .withData(Map.of(IBMMQ.MQSC_COMMAND_FILE_NAME, mqscConfig()))
            .build()
        );
        // @formatter:on
    }

    @Override
    public String externalHostname() {
        return "localhost";
    }
}
