package software.tnb.jaeger.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.Pod;

@AutoService(Jaeger.class)
public class OpenshiftJaeger extends Jaeger implements OpenshiftDeployable, WithExternalHostname, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftJaeger.class);
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

        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).resource(customResource()).create();
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
        return "https://" + OpenshiftClient.get().getRoute(JAEGER_INSTANCE_NAME).getSpec().getHost();
    }

    @Override
    public String operatorName() {
        return "jaeger-product";
    }

    @Override
    public String getLog() {
        return OpenshiftClient.get().getPodLog(servicePod().get(), "jaeger");
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

    @Override
    public String kind() {
        return "Jaeger";
    }

    @Override
    public String apiVersion() {
        return "jaegertracing.io/v1";
    }

    @Override
    public GenericKubernetesResource customResource() {
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

        return new GenericKubernetesResourceBuilder()
            .withKind(kind())
            .withApiVersion(apiVersion())
            .withNewMetadata()
            .withName(JAEGER_INSTANCE_NAME)
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", spec)).build();
    }
}
