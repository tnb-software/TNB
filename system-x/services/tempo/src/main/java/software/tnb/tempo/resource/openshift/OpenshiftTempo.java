package software.tnb.tempo.resource.openshift;

import software.tnb.aws.s3.service.Minio;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.service.ServiceFactory;
import software.tnb.common.utils.WaitUtils;
import software.tnb.tempo.service.Tempo;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;

@AutoService(Tempo.class)
public class OpenshiftTempo extends Tempo implements OpenshiftDeployable, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftTempo.class);
    private static final String INSTANCE_NAME = "tnb-tempostack";
    private static final String MINIO_SECRET_TEMPOSTACK = "minio-secret-tempostack";
    private static final String MINIO_BUCKET = "tempo";
    private static final String STORAGE_TYPE = "s3";
    private static final String STORAGE_SIZE = "1Gi";

    private final Minio minio = ServiceFactory.create(Minio.class);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        minio.beforeAll(context);
        OpenshiftDeployable.super.beforeAll(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        OpenshiftDeployable.super.afterAll(context);
        minio.afterAll(context);
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().deleteCustomResource(apiVersion().split("/")[0], apiVersion().split("/")[1], kind(), INSTANCE_NAME);
        WaitUtils.waitFor(() -> servicePods().isEmpty(), "wait until tempostack pods are terminated");
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

        //create minio storage
        createMinioStorage();

        //create stack in the current namespace
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind())
            .inNamespace(OpenshiftClient.get().getNamespace())
            .resource(customResource()).create();
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
    public String getLog() {
        return OpenshiftClient.get().getPodLog(servicePod().get());
    }

    @Override
    public String getDistributorHostname() {
        return "tempo-%s-distributor".formatted(INSTANCE_NAME);
    }

    @Override
    public String getGatewayHostname() {
        return "tempo-%s-gateway".formatted(INSTANCE_NAME);
    }

    @Override
    public String getGatewayUrl() {
        return "%s:8090".formatted(getGatewayHostname());
    }

    @Override
    public String kind() {
        return "TempoStack";
    }

    @Override
    public String apiVersion() {
        return "tempo.grafana.com/v1alpha1";
    }

    @Override
    public GenericKubernetesResource customResource() {

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
            .withKind(kind())
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
}
