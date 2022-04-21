package org.jboss.fuse.tnb.sql.common.resource.openshift;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.common.deployment.WithName;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.sql.common.service.Sql;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.LocalPortForward;

public class GenericOpenshiftDb implements OpenshiftDeployable, WithName {

    private LocalPortForward portForward;
    private final Sql sqlService;

    public GenericOpenshiftDb(Sql sqlService) {
        this.sqlService = sqlService;
    }

    @Override
    public void create() {
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
                                .withImage(sqlService.sqlImage())
                                .addNewPort()
                                    .withContainerPort(sqlService.port())
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
                        .withPort(sqlService.port())
                        .withTargetPort(new IntOrString(sqlService.port()))
                    .endPort()
                .endSpec()
            .build()
        );
        //@formatter:on

    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false)
            .areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).timeout(120_000).waitFor();
    }

    @Override
    public void openResources() {
        portForward = OpenshiftClient.get().services().withName(name()).portForward(sqlService.port(), sqlService.port());
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
