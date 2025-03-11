package software.tnb.infinispan.resource.openshift;

import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.infinispan.resource.openshift.generated.v1.InfinispanSpec;
import software.tnb.infinispan.resource.openshift.generated.v1.infinispanspec.Expose;
import software.tnb.infinispan.resource.openshift.generated.v1.infinispanspec.Security;
import software.tnb.infinispan.resource.openshift.generated.v1.infinispanspec.security.EndpointEncryption;
import software.tnb.infinispan.resource.openshift.generated.v2alpha1.Cache;
import software.tnb.infinispan.resource.openshift.generated.v2alpha1.CacheSpec;
import software.tnb.infinispan.resource.openshift.generated.v2alpha1.cachespec.Updates;
import software.tnb.infinispan.service.Infinispan;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.google.auto.service.AutoService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.SecretBuilder;

@AutoService(Infinispan.class)
public class OpenshiftInfinispan extends Infinispan implements ReusableOpenshiftDeployable, WithName, WithOperatorHub {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftInfinispan.class);

    private static final String SECRET_NAME = "tnb-infinispan-identities";
    public static final String CR_CACHE_NAME = "default-cache";

    @Override
    public void undeploy() {
        OpenshiftClient.get().genericKubernetesResources("infinispan.org/v2alpha1", "Cache").delete();
        OpenshiftClient.get().genericKubernetesResources("infinispan.org/v1", "Infinispan").delete();
        WaitUtils.waitFor(() -> OpenshiftClient.get().resources(software.tnb.infinispan.resource.openshift.generated.v1.Infinispan.class)
            .list().getItems().isEmpty(), "Waiting until the cluster has been removed");
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public void create() {
        LOG.debug("Creating Data Grid subscription");
        createSubscription();

        LOG.debug("Creating Identities secret");
        createIdentitiesSecret();

        LOG.debug("Creating Data Grid cluster");
        OpenshiftClient.get().resources(software.tnb.infinispan.resource.openshift.generated.v1.Infinispan.class)
            .resource(getInfinispanCR()).create();

        createDefaultCache();
    }

    private void createDefaultCache() {
        LOG.debug("Creating Default Cache");
        OpenshiftClient.get().resources(Cache.class).resource(getInfinispanCacheCR()).create();
    }

    private void createIdentitiesSecret() {
        final Map<String, Object> credentials = Map.of("credentials", List.of(Map.of("username", account().username()
            , "password", account().password())));

        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml dataContent = new Yaml(options);

        SecretBuilder sb = new SecretBuilder().editOrNewMetadata().withName(SECRET_NAME).endMetadata()
                .addToStringData("identities.yaml", dataContent.dump(credentials));
        OpenshiftClient.get().secrets().resource(sb.build()).create();
    }

    private software.tnb.infinispan.resource.openshift.generated.v1.Infinispan getInfinispanCR() {
        final software.tnb.infinispan.resource.openshift.generated.v1.Infinispan cluster
            = new software.tnb.infinispan.resource.openshift.generated.v1.Infinispan();
        final ObjectMeta metadata = new ObjectMeta();
        metadata.setName(name());
        cluster.setMetadata(metadata);
        final InfinispanSpec spec = new InfinispanSpec();
        spec.setReplicas(1);
        final Security security = new Security();
        final EndpointEncryption endpointEncryption = new EndpointEncryption();
        endpointEncryption.setType(EndpointEncryption.Type.NONE);
        security.setEndpointEncryption(endpointEncryption);
        security.setEndpointAuthentication(Boolean.TRUE);
        security.setEndpointSecretName(SECRET_NAME);
        spec.setSecurity(security);
        final Expose expose = new Expose();
        expose.setType(Expose.Type.ROUTE);
        spec.setExpose(expose);
        cluster.setSpec(spec);
        return cluster;
    }

    private Cache getInfinispanCacheCR() {
        final Cache cache = new Cache();
        final ObjectMeta metadata = new ObjectMeta();
        metadata.setName(CR_CACHE_NAME);
        cache.setMetadata(metadata);
        CacheSpec spec = new CacheSpec();
        spec.setName("default");
        spec.setClusterName(name());
        try (InputStream ts = Thread.currentThread().getContextClassLoader().getResourceAsStream("cache-template.yaml")) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(ts, out);
            spec.setTemplate(out.toString());
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Updates updates = new Updates();
        updates.setStrategy(Updates.Strategy.RETAIN);
        spec.setUpdates(updates);
        cache.setSpec(spec);
        return cache;
    }

    @Override
    public boolean isDeployed() {
        Optional<software.tnb.infinispan.resource.openshift.generated.v1.Infinispan> cluster = OpenshiftClient.get()
            .resources(software.tnb.infinispan.resource.openshift.generated.v1.Infinispan.class).list()
            .getItems().stream().filter(infinispan -> infinispan.getMetadata().getName().equals(name()))
            .findFirst();
        return cluster.isPresent() && !cluster.get().isMarkedForDeletion();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return pod -> OpenshiftClient.get().hasLabels(pod, Map.of("clusterName", name()));
    }

    @Override
    public void cleanup() {
        //re-create the cache
        OpenshiftClient.get().resources(Cache.class).withName(CR_CACHE_NAME).delete();
        createDefaultCache();
    }

    @Override
    public String name() {
        return "tnb-infinispan";
    }

    @Override
    public int getPortMapping() {
        return PORT;
    }

    @Override
    public String getHost() {
        return name();
    }

    @Override
    public String operatorName() {
        return "datagrid";
    }
}
