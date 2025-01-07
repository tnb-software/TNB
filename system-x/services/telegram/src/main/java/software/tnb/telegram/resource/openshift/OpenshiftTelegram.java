package software.tnb.telegram.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.telegram.service.Telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;

@AutoService(Telegram.class)
public class OpenshiftTelegram extends Telegram implements OpenshiftDeployable, WithName {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftTelegram.class);

    @Override
    public void undeploy() {
        LOG.info("Undeploying Telegram client");
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
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
        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", getEnv()
        ));
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
    public String execInContainer(String... commands) {
        try {
            return new String(servicePod().redirectingOutput().exec(commands).getOutput().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read command output: " + e);
        }
    }

    @Override
    public String name() {
        return "telegram-client";
    }
}
