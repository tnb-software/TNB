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
import software.tnb.jms.ibm.mq.validation.IBMMQValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KeyToPathBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.kubernetes.client.dsl.PodResource;
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
        LOG.debug("Deleting deployment {}", name());
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        LOG.debug("Deleting config map {}", name());
        OpenshiftClient.get().configMaps().withName(CONFIG_MAP_NAME).delete();
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
    protected String clientHostname() {
        return externalHostname();
    }

    @Override
    public IBMMQValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new IBM MQ validation");
            validation = new IBMMQValidation(account(), client(), servicePod().get());
        }
        return validation;
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
        UidSupplier supplier = new UidSupplier();
        WaitUtils.waitFor(supplier, "Waiting for uid-range openshift annotation");
        uid = supplier.getUid();

        createMqscConfigMap();

        // @formatter:off
        Map<String, Integer> ports = Map.of(name(), DEFAULT_PORT, name() + "-console", CONSOLE_PORT);
        List<ContainerPort> containerPorts = ports.entrySet().stream()
            .map(e -> new ContainerPortBuilder().withName(e.getKey()).withContainerPort(e.getValue()).withProtocol("TCP").build())
            .collect(Collectors.toList());
        List<Volume> volumes = List.of(
            new VolumeBuilder().withName(CONFIG_MAP_VOLUME_NAME)
                .withConfigMap(new ConfigMapVolumeSourceBuilder()
                    .withName(CONFIG_MAP_NAME)
                    .withItems(new KeyToPathBuilder()
                        .withKey(IBMMQ.MQSC_COMMAND_FILE_NAME)
                        .withPath(IBMMQ.MQSC_COMMAND_FILE_NAME)
                        .build()
                    )
                    .build()
                ).build()
        );
        List<VolumeMount> volumeMounts = List.of(
            new VolumeMountBuilder().withName(CONFIG_MAP_VOLUME_NAME)
                .withMountPath(IBMMQ.MQSC_COMMAND_FILES_LOCATION + "/" + IBMMQ.MQSC_COMMAND_FILE_NAME)
                .withSubPath(IBMMQ.MQSC_COMMAND_FILE_NAME).build()
        );

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnvironment(),
            "ports", containerPorts,
            "volumes", volumes,
            "volumeMounts", volumeMounts
        ), builder -> builder
            .editSpec()
                .editTemplate()
                    .editSpec()
                        .editContainer(0)
                            .withNewSecurityContext()
                                .withRunAsUser(uid)
                            .endSecurityContext()
                        .endContainer()
                    .endSpec()
                .endTemplate()
            .endSpec()
        );

        ports.forEach((key, value) -> {
            LOG.debug("Creating service {}", key);
            OpenshiftClient.get().services().resource(
                new ServiceBuilder()
                    .editOrNewMetadata()
                        .withName(key)
                        .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .endMetadata()
                    .editOrNewSpec().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())

                    .addToPorts(new ServicePortBuilder()
                        .withName(key)
                        .withPort(value)
                        .withTargetPort(new IntOrString(value))
                        .build())
                    .endSpec()
                    .build()
            ).serverSideApply();
        });

        OpenshiftClient.get().routes().resource(new RouteBuilder()
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
        ).serverSideApply();
        // @formatter:on
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("Started web server");
    }

    @Override
    public boolean isDeployed() {
        return WithName.super.isDeployed();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public String host() {
        return inClusterHostname();
    }

    @Override
    public int clientPort() {
        return OpenshiftConfiguration.isMicroshift() ? microshiftClientPort() : localPort;
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
        OpenshiftClient.get().configMaps().resource(new ConfigMapBuilder()
            .withNewMetadata()
                .withName(CONFIG_MAP_NAME)
                .addToLabels(Map.of(OpenshiftConfiguration.openshiftDeploymentLabel(), name()))
            .endMetadata()
            .withData(Map.of(IBMMQ.MQSC_COMMAND_FILE_NAME, mqscConfig()))
            .build()
        ).serverSideApply();
        // @formatter:on
    }

    @Override
    public String externalHostname() {
        return OpenshiftConfiguration.isMicroshift() ? OpenshiftClient.get().config().getMasterUrl().getHost() : "localhost";
    }

    private int microshiftClientPort() {
        return OpenshiftClient.get().getService(name()).getSpec().getPorts().stream()
            .filter(servicePort -> name().equals(servicePort.getName()))
            .findFirst().get().getNodePort();
    }

    /**
     * In some cases the code was quick enough and the annotations were empty, so moved the uid-range logic into a separate supplier.
     */
    private static final class UidSupplier implements BooleanSupplier {
        private long uid;

        @Override
        public boolean getAsBoolean() {
            Map<String, String> annotations = OpenshiftClient.get().namespaces().withName(OpenshiftClient.get().getNamespace()).get().getMetadata()
                .getAnnotations();
            
            if (annotations == null || annotations.isEmpty()) {
                return false;
            }

            final List<Long> uidRange = Arrays.stream(annotations.get("openshift.io/sa.scc.uid-range").split("/")).map(Long::parseLong).toList();
            uid = ThreadLocalRandom.current().nextLong(uidRange.get(0), uidRange.get(0) + uidRange.get(1));
            return true;
        }

        public long getUid() {
            return uid;
        }
    }
}
