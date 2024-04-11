package software.tnb.kudu.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.kudu.service.Kudu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarSource;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ObjectFieldSelector;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.client.readiness.Readiness;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(Kudu.class)
public class OpenshiftKudu extends Kudu implements OpenshiftDeployable, WithInClusterHostname {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftKudu.class);

    private final List<String> hostNames = List.of(MASTER_PREFIX, TSERVER_PREFIX);
    private static final String KUDU_CLIENT = "kudu-client";

    @Override
    public void openResources() {
        client = new RemoteClient(getMastersUsingRpc(), getClientPodName());
        LOG.debug("master leader {}", client.findLeaderMasterServer());
        createUIRoute();
    }

    private String getClientPodName() {
        return OpenshiftClient.get().pods().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), KUDU_CLIENT)
            .list().getItems().stream().filter(Readiness::isPodReady).map(p -> p.getMetadata().getName())
            .findAny().orElseThrow(() -> new IllegalStateException("unable to find a ready client pod"));
    }

    @Override
    public void closeResources() {
        if (client != null) {
            try {
                client.close();
                client = null;
            } catch (IOException e) {
                LOG.warn("unable to close the client");
            }
        }
    }

    @Override
    public void create() {
        createServices();
        createStatefulSet();
        createClient();
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getStatefulSet(MASTER_PREFIX) != null
            && OpenshiftClient.get().getStatefulSet(TSERVER_PREFIX) != null
            && servicePods().size() == getConfiguration().masterNumber() + getConfiguration().tabletNumber();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return pod -> pod.getMetadata().getLabels().entrySet().stream()
            .anyMatch(e -> e.getKey().equals(OpenshiftConfiguration.openshiftDeploymentLabel())
            && (MASTER_PREFIX.equals(e.getValue()) || TSERVER_PREFIX.equals(e.getValue()) || KUDU_CLIENT.equals(e.getValue())));
    }

    @Override
    public void undeploy() {
        LOG.debug("Delete deployment {}", KUDU_CLIENT);
        OpenshiftClient.get().apps().deployments().withName(KUDU_CLIENT).delete();

        LOG.debug("Delete route with label {}={}", OpenshiftConfiguration.openshiftDeploymentLabel(), MASTER_PREFIX);
        OpenshiftClient.get().routes().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), MASTER_PREFIX).delete();

        hostNames.forEach(label -> {
            LOG.debug("Delete stateful set {}", label);
            OpenshiftClient.get().apps().statefulSets().withName(label).delete();
            LOG.debug("Delete services with label {}={}", OpenshiftConfiguration.openshiftDeploymentLabel(), label);
            OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), label).delete();
        });

        WaitUtils.waitFor(() -> OpenshiftClient.get().pods().list().getItems().stream().filter(podSelector()).count() == 0
            , "Wait for pods terminating");
    }

    @Override
    public String inClusterHostname() {
        return String.join(",", getMastersUsingRpc());
    }

    @Override
    public List<String> getMastersUsingHttp() {
        return IntStream.range(0, getConfiguration().masterNumber())
            .mapToObj(i -> MASTER_PREFIX + "-" + i + ":" + getPorts(true).get("ui")).toList();
    }

    @Override
    public List<String> getTserversUsingHttp() {
        return IntStream.range(0, getConfiguration().tabletNumber())
            .mapToObj(i -> TSERVER_PREFIX + "-" + i + ":" + getPorts(false).get("ui")).toList();
    }

    @Override
    public List<String> getMastersUsingRpc() {
        return IntStream.range(0, getConfiguration().masterNumber())
            .mapToObj(i -> MASTER_PREFIX + "-" + i + ":" + getPorts(true).get("rpc-port")).toList();
    }

    @Override
    public List<String> getTserversUsingRpc() {
        return IntStream.range(0, getConfiguration().tabletNumber())
            .mapToObj(i -> TSERVER_PREFIX + "-" + i + ":" + getPorts(false).get("rpc-port")).toList();
    }

    private void createServices() {
        hostNames.forEach(
            serviceName -> {
                boolean isMaster = serviceName.contains("master");

                Map<String, Integer> ports = getPorts(isMaster);

                ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel()
                    , serviceName);

                ports.forEach((key, value) ->
                    serviceSpecBuilder.addToPorts(new ServicePortBuilder()
                        .withName(key)
                        .withPort(value)
                        .withTargetPort(new IntOrString(value))
                        .build())
                );

                LOG.debug("Creating service {}", serviceName);
                OpenshiftClient.get().services().resource(new ServiceBuilder()
                    .editOrNewMetadata()
                    .withName(serviceName)
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), serviceName)
                    .endMetadata()
                    .editOrNewSpecLike(serviceSpecBuilder.build())
                    .withType("NodePort")
                    .endSpec()
                    .build()).serverSideApply();
            }
        );

        //create service for single pods of masters
        IntStream.range(0, getConfiguration().masterNumber()).forEach(i -> {
            String serviceName = MASTER_PREFIX + "-" + i;
            LOG.debug("Creating service {}", serviceName);
            OpenshiftClient.get().services().resource(
                new ServiceBuilder()
                    .editOrNewMetadata()
                        .withName(serviceName)
                        .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), MASTER_PREFIX)
                    .endMetadata()
                    .editOrNewSpecLike(new ServiceSpecBuilder()
                        .addToSelector("statefulset.kubernetes.io/pod-name"
                        , serviceName)
                        .addNewPort()
                            .withName("rpc-port")
                            .withPort(getPorts(true).get("rpc-port"))
                            .withTargetPort(new IntOrString(getPorts(true).get("rpc-port")))
                        .endPort()
                        .addNewPort()
                            .withName("ui")
                            .withPort(getPorts(true).get("ui"))
                            .withTargetPort(new IntOrString(getPorts(true).get("ui")))
                        .endPort()
                        .build())
                    .endSpec()
                    .build()
            ).serverSideApply();
        });

        //create service for single pods of tserver
        IntStream.range(0, getConfiguration().tabletNumber()).forEach(i -> {
            String serviceName = TSERVER_PREFIX + "-" + i;
            LOG.debug("Creating service {}", serviceName);
            OpenshiftClient.get().services().resource(
                new ServiceBuilder()
                    .editOrNewMetadata()
                    .withName(serviceName)
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), TSERVER_PREFIX)
                    .endMetadata()
                    .editOrNewSpecLike(new ServiceSpecBuilder()
                        .addToSelector("statefulset.kubernetes.io/pod-name"
                            , serviceName)
                        .addNewPort()
                            .withName("rpc-port")
                            .withPort(getPorts(true).get("rpc-port"))
                            .withTargetPort(new IntOrString(getPorts(true).get("rpc-port")))
                        .endPort()
                        .build())
                    .endSpec()
                    .build()
            ).serverSideApply();
        });
    }

    private void createStatefulSet() {
        hostNames.forEach(
            statefulSetName -> {
                boolean isMaster = statefulSetName.contains("master");
                int replicaNum = isMaster ? getConfiguration().masterNumber() : getConfiguration().tabletNumber();
                List<ContainerPort> containerPorts = getPorts(isMaster).entrySet().stream()
                    .map(e -> new ContainerPortBuilder().withName(e.getKey()).withContainerPort(e.getValue()).withProtocol("TCP").build())
                    .collect(Collectors.toList());
                String arg = isMaster ? "master" : "tserver";
                final Probe probe = new ProbeBuilder().editOrNewHttpGet().withPort(new IntOrString(getPorts(isMaster).get("ui")))
                    .withPath("/healthz").withScheme("HTTP").endHttpGet()
                    .withInitialDelaySeconds(10)
                    .withTimeoutSeconds(5)
                    .withFailureThreshold(10).build();

                EnvVar getHostFrom = new EnvVar("GET_HOSTS_FROM", "dns", null);
                EnvVar podIp = new EnvVar("POD_IP", null, new EnvVarSource(null
                    , new ObjectFieldSelector(null, "status.podIP"), null, null));
                EnvVar podName = new EnvVar("POD_NAME", null, new EnvVarSource(null
                    , new ObjectFieldSelector(null, "metadata.name"), null, null));

                // @formatter:off
                LOG.debug("Creating Stateful Set {}", statefulSetName);
                OpenshiftClient.get().apps().statefulSets().resource(new StatefulSetBuilder()
                        .editOrNewMetadata()
                            .withName(statefulSetName)
                            .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), statefulSetName)
                        .endMetadata()
                        .editOrNewSpec()
                            .withServiceName(statefulSetName)
                            .withPodManagementPolicy("Parallel")
                            .withReplicas(replicaNum)
                            .withNewSelector()
                                .addToMatchLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), statefulSetName)
                            .endSelector()
                            .editOrNewTemplate()
                                .editOrNewMetadata()
                                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), statefulSetName)
                                .endMetadata()
                                .editOrNewSpec()
                                    .addNewContainer()
                                        .withName(statefulSetName)
                                        .withImage(image())
                                        .withImagePullPolicy("IfNotPresent")
                                        .addAllToPorts(containerPorts)
                                        .addAllToEnv(List.of(getHostFrom, podIp, podName))
                                        .addAllToEnv(containerEnvironment(isMaster).entrySet().stream()
                                            .map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                                            .collect(Collectors.toList()))
                                        .withArgs(arg)
                                        .withReadinessProbe(probe)
                                        .withLivenessProbe(probe)
                                        .addToVolumeMounts(new VolumeMountBuilder()
                                            .withName("datadir")
                                            .withMountPath("/var/lib/kudu")
                                            .build()
                                        )
                                    .endContainer()
                                    .addToVolumes(new VolumeBuilder().withNewEmptyDir().endEmptyDir().withName("datadir").build())
                                .endSpec()
                            .endTemplate()
                        .endSpec()
                    .build()
                ).serverSideApply();
                // @formatter:on
            }
        );

    }

    private String createUIRoute() {
        final String name = "kudu-ui";

        Function<String, Boolean> routeExists = n -> OpenshiftClient.get().routes().withName(n).get() != null;

        if (routeExists.apply(name)) {
            return name;
        }

        final String masterService = client.findLeaderMasterServer().split("\\.")[0];
        LOG.debug("Creating Route {} to {}", name, masterService);
        // @formatter:off
        OpenshiftClient.get().routes()
            .resource(new RouteBuilder()
            .editOrNewMetadata()
                .withName(name)
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), MASTER_PREFIX)
            .endMetadata()
            .editOrNewSpec()
                .withNewTo()
                    .withKind("Service")
                    .withName(masterService)
                    .withWeight(100)
                .endTo()
                .withNewPort()
                    .withTargetPort(new IntOrString(getPorts(true).get("ui")))
                .endPort()
            .endSpec()
            .build()
        ).serverSideApply();
        // @formatter:on

        WaitUtils.waitFor(() -> routeExists.apply(name), "Waiting for route is created");

        return name;
    }

    private Map<String, String> containerEnvironment(boolean isMaster) {
        return Map.of("KUDU_MASTERS", String.join(",", getMastersUsingRpc())
                , isMaster ? "MASTER_ARGS" : "TSERVER_ARGS"
                , String.format("--webserver_port=%s --stderrthreshold=0 --use_hybrid_clock=false --unlock_unsafe_flags=true"
                , getPorts(isMaster).get("ui"))
            );
    }

    private static Map<String, Integer> getPorts(boolean isMaster) {
        return Map.of("ui", isMaster ? MASTER_HTTP_PORT : TSERVER_HTTP_PORT
            , "rpc-port", isMaster ? MASTER_RPC_PORT : TSERVER_RPC_PORT);
    }

    private void createClient() {
        // @formatter:off
        LOG.debug("Creating deployment for client");
        OpenshiftClient.get().apps().deployments().resource(new DeploymentBuilder()
                .editOrNewMetadata()
                    .withName(KUDU_CLIENT)
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), KUDU_CLIENT)
                .endMetadata()
                .editOrNewSpec()
                    .withNewSelector()
                        .addToMatchLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), KUDU_CLIENT)
                    .endSelector()
                    .withReplicas(1)
                    .editOrNewTemplate()
                        .editOrNewMetadata()
                            .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), KUDU_CLIENT)
                        .endMetadata()
                        .editOrNewSpec()
                            .addNewContainer()
                                .withName(KUDU_CLIENT)
                                .withImage(image())
                                .withCommand("tail", "-f", "/dev/null")
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build()
        ).serverSideApply();
        // @formatter:on
    }
}
