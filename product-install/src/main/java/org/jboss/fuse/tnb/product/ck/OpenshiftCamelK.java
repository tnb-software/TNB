package org.jboss.fuse.tnb.product.ck;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.Product;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

@AutoService(Product.class)
public class OpenshiftCamelK extends Product implements OpenshiftDeployable {
    private static final CustomResourceDefinitionContext INTEGRATIONS_CONTEXT = new CustomResourceDefinitionContext.Builder()
        .withGroup("camel.apache.org")
        .withPlural("integrations")
        .withScope("Namespaced")
        .withVersion("v1")
        .build();

    @Override
    public void create() {
        OpenshiftClient.createSubscription("stable", "camel-k", "community-operators", "test-camel-k");
        OpenshiftClient.waitForCompletion("test-camel-k");
    }

    @Override
    public void undeploy() {
        OpenshiftClient.deleteSubscription("test-camel-k");
    }

    public void deployIntegration(Object route) {
        try {
            OpenshiftClient.get().customResource(INTEGRATIONS_CONTEXT).create(OpenshiftConfiguration.openshiftNamespace(), createIntegrationResource("myroutebuilder", (String) route));
        } catch (IOException e) {
            e.printStackTrace();
        }
        waitForIntegration();
    }

    public void waitForIntegration() {
        try {
            WaitUtils.waitFor(() -> {
                JSONObject integration = new JSONObject(OpenshiftClient.get().customResource(INTEGRATIONS_CONTEXT)
                    .get(OpenshiftConfiguration.openshiftNamespace(), "myroutebuilder"));
                try {
                    return "running".equalsIgnoreCase(integration.getJSONObject("status").getString("phase"));
                } catch (JSONException ignored) {
                    return false;
                }
            }, 60, 5000L);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void undeployIntegration() {
        try {
            OpenshiftClient.get().customResource(INTEGRATIONS_CONTEXT).delete(OpenshiftConfiguration.openshiftNamespace(), "myroutebuilder");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> createIntegrationResource(String name, String routeDefinition) {
        Map<String, String> content = MapUtils.map("name", name + ".java", "content",
            "// camel-k: language=java\n" +
                "\n" +
                routeDefinition);
        Map<String, Object> spec = new HashMap<>();
        List<Map<String, String>> sources = new ArrayList<>();
        sources.add(content);
        spec.put("sources", sources);

        Map<String, String> metadata = MapUtils.map("name", name);
        Map<String, Object> integration = new HashMap<>();
        integration.put("kind", "Integration");
        integration.put("apiVersion", "camel.apache.org/v1");
        integration.put("metadata", metadata);
        integration.put("spec", spec);
        return integration;
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("name", "camel-k-operator"));
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods("name", "camel-k-operator").size() != 0;
    }
}
