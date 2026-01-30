package software.tnb.tempo.resource.openshift;

import software.tnb.aws.s3.service.Minio;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.service.ServiceFactory;
import software.tnb.common.utils.StringUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.tempo.service.Tempo;
import software.tnb.tempo.validation.TempoValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(Tempo.class)
public class OpenshiftTempo extends Tempo implements OpenshiftDeployable, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftTempo.class);
    private static final String INSTANCE_NAME = "tnb-tempo-" + StringUtils.getRandomAlphanumStringOfLength(4);
    private static final String MINIO_SECRET_TEMPOSTACK = "minio-" + INSTANCE_NAME;
    private static final String MINIO_BUCKET = "tempo";
    private static final String STORAGE_TYPE = "s3";
    private static final String STORAGE_SIZE = "1Gi";
    private static final String KIND_TEMPOSTACK = "TempoStack";
    private static final String KIND_TEMPOMONOLITHIC = "TempoMonolithic";

    private final Minio minio = ServiceFactory.create(Minio.class);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // Only start MinIO if using TempoStack
        if (!getConfiguration().isMonolithic()) {
            minio.beforeAll(context);
        }
        OpenshiftDeployable.super.beforeAll(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        OpenshiftDeployable.super.afterAll(context);
        // Only clean up MinIO if it was started
        if (!getConfiguration().isMonolithic()) {
            minio.afterAll(context);
        }
    }

    @Override
    public void undeploy() {
        // Only undeploy if the resource exists
        if (OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind())
            .inNamespace(OpenshiftClient.get().getNamespace())
            .withName(INSTANCE_NAME)
            .get() != null) {
            OpenshiftClient.get().deleteCustomResource(apiVersion().split("/")[0], apiVersion().split("/")[1], kind(), INSTANCE_NAME);
            WaitUtils.waitFor(() -> servicePods().isEmpty(), 60, 5000L, "wait until tempo pods are terminated");
        }
    }

    @Override
    public TempoValidation validation() {
        validation = Optional.ofNullable(validation)
            .orElseGet(() -> new TempoValidation(getGatewayExternalUrl()
                , OpenshiftClient.get().getServiceAccountAuthToken(getConfiguration().isMonolithic()
                    ? "tempo-" + INSTANCE_NAME : getGatewayHostname())));
        return validation;
    }

    @Override
    public void openResources() {
    }

    @Override
    public String targetNamespace() {
        return "openshift-tempo-operator";
    }

    @Override
    public void closeResources() {

    }

    @Override
    public void create() {
        LOG.debug("Creating Tempo subscription");
        //create target namespace if not exists
        OpenshiftClient.get().createNamespace(targetNamespace());

        // Create subscription if needed
        createSubscription();

        // Create Roles to allow the traces to be read/written
        createRoles();

        // Create MinIO storage only for TempoStack
        if (!getConfiguration().isMonolithic()) {
            createMinioStorage();
        }

        //create stack in the current namespace
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind())
            .inNamespace(OpenshiftClient.get().getNamespace())
            .resource(customResource()).create();

        // Create route for TempoMonolithic
        if (getConfiguration().isMonolithic()) {
            createRoute();
        }
    }

    private void createRoles() {
        OpenshiftClient.get().serverSideApply("/software/tnb/tempo/resource/openshift/00-CR-tracereader.yaml");
        OpenshiftClient.get().serverSideApply("/software/tnb/tempo/resource/openshift/01-CRB-tracereader.yaml");
        OpenshiftClient.get().serverSideApply("/software/tnb/tempo/resource/openshift/02-CR-tracewriter.yaml");
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().apps().deployments()
            .inNamespace(targetNamespace()).list().getItems().stream()
            .anyMatch(deployment -> INSTANCE_NAME.equals(deployment.getMetadata().getName()));
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("app.kubernetes.io/managed-by", "tempo-operator"));
    }

    @Override
    public boolean clusterWide() {
        return true;
    }

    @Override
    public String operatorName() {
        return "tempo-product";
    }

    @Override
    public String getDistributorHostname() {
        if (getConfiguration().isMonolithic()) {
            return "tempo-" + INSTANCE_NAME;
        } else {
            return "tempo-%s-distributor".formatted(INSTANCE_NAME);
        }
    }

    @Override
    public String getGatewayHostname() {
        return "tempo-%s-gateway".formatted(INSTANCE_NAME);
    }

    @Override
    public String getGatewayUrl() {
        if (getConfiguration().isMonolithic()) {
            // TempoMonolithic uses port 4317 for Tempo API/Gateway GRPC
            return "%s:4317".formatted(getGatewayHostname());
        } else {
            // TempoStack gateway uses port 8090
            return "%s:8090".formatted(getGatewayHostname());
        }
    }

    @Override
    public String getGatewayExternalUrl() {
        return "https://" + OpenshiftClient.get().routes().inNamespace(OpenshiftClient.get().getNamespace())
            .withName(getGatewayHostname()).get().getSpec().getHost();
    }

    @Override
    public String kind() {
        return getConfiguration().isMonolithic() ? KIND_TEMPOMONOLITHIC : KIND_TEMPOSTACK;
    }

    @Override
    public String apiVersion() {
        return "tempo.grafana.com/v1alpha1";
    }

    @Override
    public GenericKubernetesResource customResource() {
        if (getConfiguration().isMonolithic()) {
            return createTempoMonolithicResource();
        } else {
            return createTempoStackResource();
        }
    }

    private GenericKubernetesResource createTempoMonolithicResource() {
        Map<String, Object> spec = new HashMap<>();

        // Resources
        Map<String, Object> resources = new HashMap<>();
        resources.put("limits", Map.of(
            "cpu", getConfiguration().getResourceLimitsCpu(),
            "memory", getConfiguration().getResourceLimitsMemory()
        ));
        spec.put("resources", resources);

        // Storage - use in-memory backend
        Map<String, Object> storage = new HashMap<>();
        Map<String, Object> traces = new HashMap<>();
        traces.put("backend", "memory");
        storage.put("traces", traces);
        spec.put("storage", storage);

        // Multitenancy (from reference tempo.yaml)
        Map<String, Object> multitenancy = new HashMap<>();
        multitenancy.put("enabled", true);
        multitenancy.put("mode", "openshift");
        multitenancy.put("authentication", List.of(
            Map.of("tenantId", "application", "tenantName", "application")
        ));
        spec.put("multitenancy", multitenancy);

        // Tenants (required for multitenancy)
        Map<String, Object> tenants = new HashMap<>();
        tenants.put("mode", "openshift");
        tenants.put("authentication", List.of(
            Map.of("tenantId", "application", "tenantName", "application")
        ));
        spec.put("tenants", tenants);

        return new GenericKubernetesResourceBuilder()
            .withKind(KIND_TEMPOMONOLITHIC)
            .withApiVersion(apiVersion())
            .withNewMetadata()
            .withName(INSTANCE_NAME)
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", spec))
            .build();
    }

    private GenericKubernetesResource createTempoStackResource() {
        Map<String, Object> spec = new HashMap<>();
        Map<String, Object> resources = new HashMap<>();
        Map<String, Object> resourcesTotal = new HashMap<>();
        resourcesTotal.put("limits", Map.of("cpu", getConfiguration().getResourceLimitsCpu()
            , "memory", getConfiguration().getResourceLimitsMemory()));
        resources.put("total", resourcesTotal);
        spec.put("resources", resources);
        spec.put("managementState", "Managed");
        Map<String, Object> storage = new HashMap<>();
        storage.put("secret", Map.of("type", STORAGE_TYPE
            , "name", MINIO_SECRET_TEMPOSTACK));
        spec.put("storage", storage);
        spec.put("storageSize", STORAGE_SIZE);
        spec.put("replicationFactor", 1);
        Map<String, Object> template = new HashMap<>();
        template.put("gateway", Map.of("enabled", true));
        spec.put("template", template);
        Map<String, Object> tenants = new HashMap<>();
        tenants.put("mode", "openshift");
        tenants.put("authentication", List.of(Map.of("tenantId", "application", "tenantName", "application")));
        spec.put("tenants", tenants);

        return new GenericKubernetesResourceBuilder()
            .withKind(KIND_TEMPOSTACK)
            .withApiVersion(apiVersion())
            .withNewMetadata()
            .withName(INSTANCE_NAME)
            .endMetadata()
            .withAdditionalProperties(Map.of("spec", spec)).build();
    }

    private void createMinioStorage() {
        Map<String, String> config = Map.of("access_key_id", minio.account().accessKey()
            , "access_key_secret", minio.account().secretKey()
            , "endpoint", minio.hostname()
            , "bucket", MINIO_BUCKET);

        //create needed bucket
        minio.validation().createS3Bucket(MINIO_BUCKET);
        minio.validation().waitForBucket(MINIO_BUCKET);

        Secret storageSecret = new SecretBuilder()
            .withStringData(config)
            .withNewMetadata()
            .withName(MINIO_SECRET_TEMPOSTACK)
            .endMetadata().build();
        OpenshiftClient.get().secrets().inNamespace(OpenshiftClient.get().getNamespace())
            .resource(storageSecret).create();
    }

    private void createRoute() {
        if (OpenshiftClient.get().routes().withName(getGatewayHostname()).get() == null) {
            OpenshiftClient.get().routes().resource(new RouteBuilder()
                .editOrNewMetadata()
                .withName(getGatewayHostname())
                .endMetadata()
                .editOrNewSpec()
                    .withNewTo()
                        .withKind("Service")
                        .withName(getGatewayHostname())
                        .withWeight(100)
                    .endTo()
                    .editOrNewPort()
                        .withTargetPort(new IntOrString(8080))
                    .endPort()
                    .withNewTls()
                        .withTermination("passthrough")
                    .endTls()
                    .withWildcardPolicy("None")
                .endSpec()
                .build()).create();
        }
    }
}
