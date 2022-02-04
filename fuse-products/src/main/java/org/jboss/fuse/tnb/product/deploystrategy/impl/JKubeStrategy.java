package org.jboss.fuse.tnb.product.deploystrategy.impl;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.SpringBootConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftBaseDeployer;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftDeployStrategy;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftDeployStrategyType;
import org.jboss.fuse.tnb.product.util.maven.BuildRequest;
import org.jboss.fuse.tnb.product.util.maven.Maven;

import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(OpenshiftDeployStrategy.class)
public class JKubeStrategy extends OpenshiftBaseDeployer {

    @Override
    public ProductType[] products() {
        return new ProductType[]{ProductType.CAMEL_SPRINGBOOT};
    }

    @Override
    public OpenshiftDeployStrategyType deployType() {
        return OpenshiftDeployStrategyType.JKUBE;
    }

    public void deploy(String name) {
        final String plugin = String.format("%s:openshift-maven-plugin:%s"
            , SpringBootConfiguration.openshiftMavenPluginGroupId()
            , SpringBootConfiguration.openshiftMavenPluginVersion());
        final BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
            .withBaseDirectory(TestConfiguration.appLocation().resolve(name))
            .withProperties(Map.of(
                "skipTests", "true"
                , "jkube.namespace", OpenshiftConfiguration.openshiftNamespace()
                , "jkube.masterUrl", OpenshiftConfiguration.openshiftUrl() != null ? OpenshiftConfiguration.openshiftUrl()
                    : OpenshiftClient.get().getMasterUrl().toString()
                , "jkube.username", OpenshiftConfiguration.openshiftUsername()
                , "jkube.generator.from", SpringBootConfiguration.openshiftBaseImage()
                , "jkube.build.recreate", "all"
            )).withGoals("clean", "package", String.format("%s:resource", plugin)
                , String.format("%s:build", plugin), String.format("%s:apply", plugin))
            .withProfiles("openshift")
            .withLogFile(TestConfiguration.appLocation().resolve(name + "-deploy.log"));
        Maven.invoke(requestBuilder.build());
    }
}
