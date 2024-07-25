package software.tnb.opentelemetry.resource.openshift;

import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.opentelemetry.service.OpenTelemetryInstrumentation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Map;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.client.dsl.Resource;

@AutoService(OpenTelemetryInstrumentation.class)
public class OpenshiftOpenTelemetryInstrumentation extends OpenTelemetryInstrumentation implements WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftOpenTelemetryInstrumentation.class);
    private static final String OTEL_INSTR_INSTANCE_NAME = "tnb-instrumentation";

    public void create() {
        LOG.debug("Creating OpenTelemetryInstrumentation custom resource");
        getCr().create();
    }

    public void delete() {
        LOG.debug("Deleting OpenTelemetryInstrumentation custom resource");
        getCr().delete();
    }

    private Resource<GenericKubernetesResource> getCr() {
        return OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).inNamespace(OpenshiftClient.get().getNamespace())
            .resource(customResource());
    }

    @Override
    public String kind() {
        return "Instrumentation";
    }

    @Override
    public String apiVersion() {
        return "opentelemetry.io/v1alpha1";
    }

    @Override
    public GenericKubernetesResource customResource() {
        return new GenericKubernetesResourceBuilder()
            .withKind(kind())
            .withApiVersion(apiVersion())
            .withNewMetadata()
            .withName(OTEL_INSTR_INSTANCE_NAME)
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", getConfiguration().getCollectorConfigurationAsMap())).build();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        delete();
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        create();
    }
}
