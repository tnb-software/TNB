package org.jboss.fuse.tnb.mongodb.resource.openshift;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.deployment.ReusableOpenshiftDeployable;
import org.jboss.fuse.tnb.common.deployment.WithName;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.mongodb.service.MongoDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;

@AutoService(MongoDB.class)
public class OpenshiftMongoDB extends MongoDB implements ReusableOpenshiftDeployable, WithName {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftMongoDB.class);

    private PortForward portForward;

    @Override
    public void create() {
        LOG.info("Deploying OpenShift MongoDB");
        List<ContainerPort> ports = new LinkedList<>();
        ports.add(new ContainerPortBuilder()
            .withName("mongodb")
            .withContainerPort(port())
            .withProtocol("TCP").build());
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
                            .withImage(mongoDbImage())
                            .addAllToPorts(ports)
                            .addAllToEnv(containerEnvironment().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                                .collect(Collectors.toList()))
                        .endContainer()
                    .endSpec()
                .endTemplate()
                .addNewTrigger()
                    .withType("ConfigChange")
                .endTrigger()
            .endSpec()
            .build()
        );

        ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name());

        serviceSpecBuilder.addToPorts(new ServicePortBuilder()
            .withName(name())
            .withPort(port())
            .withTargetPort(new IntOrString(port()))
            .build());

        LOG.debug("Creating service {}", name());
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
        // @formatter:on
    }

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift MongoDB");
        LOG.debug("Deleting service {}", name());
        OpenshiftClient.get().services().withName(name()).delete();
        LOG.debug("Deleting deploymentconfig {}", name());
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .timeout(120_000).waitFor();
    }

    @Override
    public void openResources() {
        LOG.debug("Creating port-forward to {} for port {}", name(), port());
        portForward = OpenshiftClient.get().services().withName(name()).portForward(port(), port());
        LOG.debug("Creating new MongoClient instance");
        client = MongoClients.create(replicaSetUrl().replace("@" + hostname(), "@localhost"));
    }

    @Override
    public boolean isReady() {
        List<Pod> pods = OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
        if (ResourceFunctions.areExactlyNPodsReady(1).apply(pods)) {
            return OpenshiftClient.get().getLogs(pods.get(0)).contains("Transition to primary complete; database writes are now permitted");
        }
        return false;
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).size() != 0
            && isReady();
    }

    @Override
    public String name() {
        return "mongodb";
    }

    @Override
    protected MongoClient client() {
        return client;
    }

    @Override
    public String replicaSetUrl() {
        return String.format("mongodb://%s:%s@%s:%d/%s", account().username(), account().password(), hostname(), port(), account().database());
    }

    @Override
    public String hostname() {
        return OpenshiftClient.get().getClusterHostname(name());
    }

    @Override
    public void cleanup() {
        LOG.info("Cleaning MongoDB database");
        MongoDatabase db = client().getDatabase(account().database());
        for (String collection : db.listCollectionNames()) {
            LOG.debug("Dropping collection {}", collection);
            db.getCollection(collection).drop();
        }
    }

    @Override
    public void closeResources() {
        if (client != null) {
            LOG.debug("Closing MongoDB client");
            client.close();
        }
        if (portForward != null && portForward.isAlive()) {
            LOG.debug("Closing port-forward");
            IOUtils.closeQuietly(portForward);
        }
    }
}
