package org.jboss.fuse.tnb.product.deploystrategy;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.product.interfaces.OpenshiftDeployer;
import org.jboss.fuse.tnb.product.log.Log;
import org.jboss.fuse.tnb.product.log.OpenshiftLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class OpenshiftBaseDeployer implements OpenshiftDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftBaseDeployer.class);

    @Override
    public Log getLog(String app) {
        final String openshiftDeploymentLabel = OpenshiftConfiguration.openshiftDeploymentLabel();
        return new OpenshiftLog(p -> p.getMetadata().getLabels().containsKey(openshiftDeploymentLabel)
            && app.equals(p.getMetadata().getLabels().get(openshiftDeploymentLabel)), app);
    }

    @Override
    public void undeploy(String name) {
        LOG.info("Undeploy integration resources");
        final Map<String, String> labelMap = Map.of(OpenshiftConfiguration.openshiftDeploymentLabel(), name);
        //builds
        //delete build's pod
        OpenshiftClient.get().builds().withLabels(labelMap).list()
            .getItems().stream().forEach(build -> OpenshiftClient.get().pods()
                .withLabels(Map.of("openshift.io/build.name", build.getMetadata().getName())).delete()
            );
        OpenshiftClient.get().builds().withLabels(labelMap).delete();
        OpenshiftClient.get().buildConfigs().withLabels(labelMap).delete();
        OpenshiftClient.get().imageStreams().withLabels(labelMap).delete();
        //app
        OpenshiftClient.get().deploymentConfigs().withLabels(labelMap).delete();
        //network
        OpenshiftClient.get().services().withLabels(labelMap).delete();
        OpenshiftClient.get().routes().withLabels(labelMap).delete();
        //remaining pods
        OpenshiftClient.get().pods().withLabels(labelMap).delete();
    }
}
