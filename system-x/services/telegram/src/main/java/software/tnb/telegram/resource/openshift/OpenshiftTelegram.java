package software.tnb.telegram.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.telegram.service.Telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.DeploymentTriggerImageChangeParams;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.openshift.api.model.ImageStreamBuilder;

@AutoService(OpenshiftTelegram.class)
public class OpenshiftTelegram extends Telegram implements OpenshiftDeployable, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftTelegram.class);

    @Override
    public void undeploy() {
        LOG.info("Undeploying Telegram client");
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .timeout(120_000).waitFor();
        OpenshiftClient.get().imageStreams().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public void create() {
        LOG.info("Deploying Telegram client");

        ImageStream imageStream = new ImageStreamBuilder()
            .withNewMetadata()
            .withName(name())
            .endMetadata()
            .withNewSpec()
            .addNewTag()
            .withName("latest")
            .withFrom(new ObjectReferenceBuilder().withKind("DockerImage").withName(image()).build())
            .endTag()
            .endSpec()
            .build();

        OpenshiftClient.get().imageStreams().createOrReplace(imageStream);

        LOG.debug("Creating deployment {}", name());
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
                .withImagePullPolicy("IfNotPresent")
                .addAllToEnv(getEnv().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                    .collect(Collectors.toList()))
                .endContainer()
                .endSpec()
                .endTemplate()
                .addNewTrigger()
                .withType("ConfigChange")
                .endTrigger()
                .addNewTrigger()
                .withType("ImageChange")
                .withImageChangeParams(
                    new DeploymentTriggerImageChangeParams(true, Arrays.asList(name()),
                        new ObjectReferenceBuilder().withKind("ImageStreamTag").withName(name() + ":latest").build()
                        , null)
                )
                .endTrigger()
                .endSpec()
                .build()
        );
    }

    @Override
    public boolean isReady() {
        List<Pod> pods = OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
        return ResourceFunctions.areExactlyNPodsReady(1).apply(pods);
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).size() != 0;
    }

    @Override
    public String execInContainer(String... commands) {
        ByteArrayOutputStream baosOutput = new ByteArrayOutputStream();

        OpenshiftClient.get().pods().withName(OpenshiftClient.get().getAnyPod(name()).getMetadata().getName())
            .writingOutput(baosOutput).exec(commands);

        return baosOutput.toString();
    }

    @Override
    public String name() {
        return "telegram-client";
    }
}
