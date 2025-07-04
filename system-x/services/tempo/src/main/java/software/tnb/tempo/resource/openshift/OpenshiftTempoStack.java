package software.tnb.tempo.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.tempo.service.TempoStack;

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

@AutoService(TempoStack.class)
public class OpenshiftTempoStack extends TempoStack implements OpenshiftDeployable, WithOperatorHub, WithCustomResource {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftTempoStack.class);
    private static final String INSTANCE_NAME = "tnb-tempostack";

    @Override
    public void undeploy() {
        //cluster wide operator can be in use
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
        LOG.debug("Creating TempoStack subscription");
        //create target namespace if not exists
        OpenshiftClient.get().createNamespace(targetNamespace());

        // Create subscription if needed
        createSubscription();

        // Create Roles to allow the traces to be read/written
        createRoles();

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
    public String kind() {
        return "TempoStack";
    }

    @Override
    public String apiVersion() {
        return "tempo.grafana.com/v1alpha1";
    }

    @Override
    public GenericKubernetesResource customResource() {
        //create secret
        if (getConfiguration().getStorageSecretKeys() != null) {

            Map<String, String> secretConfig = getConfiguration().getStorageSecretKeys();

            //create needed bucket
            getConfiguration().getMinioStorage().validation().createS3Bucket(secretConfig.get("bucket"));
            getConfiguration().getMinioStorage().validation().waitForBucket(secretConfig.get("bucket"));

            Secret storageSecret = new SecretBuilder()
                .withStringData(secretConfig)
                .withNewMetadata()
                    .withName(secretConfig.get("name"))
                .endMetadata().build();
            OpenshiftClient.get().secrets().inNamespace(OpenshiftClient.get().getNamespace())
                .resource(storageSecret).create();
        }

        Map<String, Object> spec = new HashMap<>();
        Map<String, Object> resources = new HashMap<>();
        Map<String, Object> resourcesTotal = new HashMap<>();
        resourcesTotal.put("limits", Map.of("cpu", getConfiguration().getResourceLimitsCpu()
            , "memory", getConfiguration().getResourceLimitsMemory()));
        resources.put("total", resourcesTotal);
        spec.put("resources", resources);
        spec.put("managementState", "Managed");
        Map<String, Object> storage = new HashMap<>();
        storage.put("secret", Map.of("type", getConfiguration().getStorageType()
            , "name", getConfiguration().getStorageSecret()));
        spec.put("storage", storage);
        spec.put("storageSize", getConfiguration().getStorageSize());
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
}
