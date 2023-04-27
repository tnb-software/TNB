package software.tnb.jaeger.resource.openshift;

import static org.junit.jupiter.api.Assertions.fail;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.jaeger.client.JaegerClient;
import software.tnb.jaeger.client.UnauthenticatedJaegerClient;
import software.tnb.jaeger.service.Jaeger;
import software.tnb.jaeger.service.configuration.JaegerConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

@AutoService(Jaeger.class)
public class OpenshiftJaeger extends Jaeger implements OpenshiftDeployable, WithExternalHostname, WithOperatorHub {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftJaeger.class);
    private static final String CRD_GROUP = "jaegertracing.io";
    private static final String CRD_VERSION = "v1";
    private static final CustomResourceDefinitionContext JAEGER_CTX = new CustomResourceDefinitionContext.Builder()
        .withName("Jaeger")
        .withGroup(CRD_GROUP)
        .withVersion(CRD_VERSION)
        .withPlural("jaegers")
        .withScope("Namespaced")
        .build();
    private static final String JAEGER_INSTANCE_NAME = "jaeger-all-in-one-inmemory";

    @Override
    public void undeploy() {
        //cluster wide operator can be in use
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {
        validation = null;
    }

    @Override
    public void create() {
        LOG.debug("Creating Jaeger subscription");
        // Create subscription if needed
        createSubscription();
        try {
            OpenshiftClient.get().customResource(JAEGER_CTX).inNamespace(targetNamespace())
                .createOrReplace(createCr(JAEGER_INSTANCE_NAME));
        } catch (IOException e) {
            fail("Unable to create Jaeger instance", e);
        }
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().apps().deployments()
            .inNamespace(targetNamespace()).list().getItems().stream()
            .anyMatch(deployment -> JAEGER_INSTANCE_NAME.equals(deployment.getMetadata().getName()));
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("app", "jaeger", "app.kubernetes.io/name", JAEGER_INSTANCE_NAME));
    }

    @Override
    public String externalHostname() {
        return "https://" + OpenshiftClient.get()
            .routes().withName(JAEGER_INSTANCE_NAME).get()
            .getSpec().getHost();
    }

    @Override
    public String operatorName() {
        return "jaeger-product";
    }

    @Override
    public String getLog() {
        return OpenshiftClient.get().getLogs(servicePod().get(), "jaeger");
    }

    @Override
    public String getCollectorUrl(JaegerConfiguration.CollectorPort port) {
        return String.format("http://%s-collector:%s", JAEGER_INSTANCE_NAME, port.portNumber());
    }

    @Override
    public String getQueryUrl(JaegerConfiguration.QueryPort port) {
        return String.format("http://%s-query:%s", JAEGER_INSTANCE_NAME, port.portNumber());
    }

    @Override
    public String getExternalUrl() {
        return externalHostname();
    }

    @Override
    protected JaegerClient client() {
        return new UnauthenticatedJaegerClient(getExternalUrl());
    }

    @Override
    public boolean clusterWide() {
        return true;
    }

    private Map<String, Object> createCr(String name) {
        Map<String, Object> cr = new HashMap<>();
        cr.put("apiVersion", CRD_GROUP + "/" + CRD_VERSION);
        cr.put("kind", "Jaeger");
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", name);
        metadata.put("namespace", targetNamespace());
        cr.put("metadata", metadata);
        Map<String, Object> spec = new HashMap<>();
        spec.put("strategy", "allInOne");
        if (env().containsKey("PROMETHEUS_SERVER_URL")) {
            Map<String, Object> allInOneConfig = new HashMap<>();
            Map<String, Object> optionsConfig = new HashMap<>();
            optionsConfig.put("prometheus", Map.of("server-url", env().get("PROMETHEUS_SERVER_URL")));
            allInOneConfig.put("metricsStorage", Map.of("type", "prometheus"));
            allInOneConfig.put("options", optionsConfig);
            spec.put("allInOne", allInOneConfig);
        }
        spec.put("ingress", Map.of("security", "none")); //to avoid authentication
        cr.put("spec", spec);
        return cr;
    }
}
