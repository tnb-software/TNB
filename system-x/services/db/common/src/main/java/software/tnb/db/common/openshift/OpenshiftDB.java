package software.tnb.db.common.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.MapUtils;
import software.tnb.db.common.service.SQL;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.SecurityContextBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.LocalPortForward;
import io.fabric8.openshift.api.model.SecurityContextConstraints;
import io.fabric8.openshift.api.model.SecurityContextConstraintsBuilder;

public class OpenshiftDB implements OpenshiftDeployable, WithName {

    private LocalPortForward portForward;
    private final SQL sqlService;
    private final int port;
    private static final String SCC_NAME = "tnb-openshift-db";

    public OpenshiftDB(SQL sqlService, int port) {
        this.sqlService = sqlService;
        this.port = port;
    }

    @Override
    public void create() {
        SecurityContextConstraints scc = OpenshiftClient.get().securityContextConstraints().withName(SCC_NAME).get();
        if (scc == null) {
            SecurityContextConstraints restricted = OpenshiftClient.get().securityContextConstraints().withName("restricted").get();
            scc = OpenshiftClient.get().securityContextConstraints().create(
                new SecurityContextConstraintsBuilder(restricted)
                    .withNewMetadata() // new metadata to override the existing annotations
                    .withName(SCC_NAME)
                    .endMetadata()
                    .build());
        }
        final String group = "system:serviceaccounts:" + OpenshiftConfiguration.openshiftNamespace();
        scc.getGroups().add(group);
        OpenshiftClient.get().securityContextConstraints().withName(SCC_NAME).patch(scc);
        //@formatter:off
        OpenshiftClient.get().apps().deployments().createOrReplace(

            new DeploymentBuilder()
                .withNewMetadata()
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
                        .withNewSpec()
                            .addNewContainer()
                                .withName(name())
                                .withImage(sqlService.image())
                                .withSecurityContext(new SecurityContextBuilder().withAllowPrivilegeEscalation(true).build())
                                .addNewPort()
                                    .withContainerPort(port)
                                    .withName(name())
                                .endPort()
                                .withImagePullPolicy("IfNotPresent")
                                .withEnv(MapUtils.toEnvVars(sqlService.containerEnvironment()))
                                .withNewReadinessProbe()
                                    .withNewTcpSocket()
                                        .withNewPort(name())
                                    .endTcpSocket()
                                    .withInitialDelaySeconds(5)
                                    .withTimeoutSeconds(5)
                                    .withFailureThreshold(6)
                                .endReadinessProbe()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
            .build()
        );
        //@formatter:on

        //@formatter:off
        OpenshiftClient.get().services().createOrReplace(
            new ServiceBuilder()
                .withNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .withNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .addNewPort()
                        .withName(name())
                        .withPort(port)
                        .withTargetPort(new IntOrString(port))
                    .endPort()
                .endSpec()
            .build()
        );
        //@formatter:on

    }

    @Override
    public void undeploy() {
        SecurityContextConstraints scc = OpenshiftClient.get().securityContextConstraints().withName(SCC_NAME).get();
        scc.getGroups().remove("system:serviceaccounts:" + OpenshiftConfiguration.openshiftNamespace());
        OpenshiftClient.get().securityContextConstraints().withName(SCC_NAME).patch(scc);
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false)
            .areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).timeout(120_000).waitFor();
    }

    @Override
    public void openResources() {
        portForward = OpenshiftClient.get().services().withName(name()).portForward(port, port);
    }

    @Override
    public void closeResources() {
        IOUtils.closeQuietly(portForward);
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1)
            .apply(OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()));
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).size() > 0;
    }

    @Override
    public String name() {
        return sqlService.name();
    }
}
