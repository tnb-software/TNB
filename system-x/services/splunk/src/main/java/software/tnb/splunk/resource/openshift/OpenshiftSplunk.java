package software.tnb.splunk.resource.openshift;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithCustomResource;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.splunk.account.SplunkAccount;
import software.tnb.splunk.service.Splunk;
import software.tnb.splunk.service.configuration.SplunkProtocol;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.rbac.ClusterRole;
import io.fabric8.kubernetes.client.dsl.NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(Splunk.class)
public class OpenshiftSplunk extends Splunk implements OpenshiftDeployable, WithCustomResource, WithName {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftSplunk.class);
    private static final String CRD_API = "v4";
    private static final String SERVICE_API_PORT = "https-splunkd";
    private static final String ROUTE_NAME = "api";
    private final String serviceName = "splunk-" + name() + "-standalone-service";
    private List<HasMetadata> createdResources;
    private Route apiRoute;
    private String sccName;

    @Override
    public void defaultConfiguration() {
        getConfiguration().protocol(SplunkProtocol.HTTPS);
    }

    @Override
    public void create() {
        LOG.info("Deploying OpenShift Splunk");
        sccName = "tnb-splunk-" + OpenshiftClient.get().getNamespace();

        OpenshiftClient.get().addUsersToSecurityContext(OpenshiftClient.get().createSecurityContext(sccName, "nonroot"),
            OpenshiftClient.get().getServiceAccountRef("splunk-operator-controller-manager"), OpenshiftClient.get().getServiceAccountRef("default"));

        try {
            // test if CRD already exists on cluster or if there is the latest version
            if (getSplunkCrd() == null || getSplunkCrd().getSpec().getVersions().stream()
                .noneMatch(crdVersion -> CRD_API.equals(crdVersion.getName()))) {
                LOG.info("Creating Splunk CRD's from splunk-crds.yaml");
                final NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> resources =
                    OpenshiftClient.get().load(this.getClass().getResourceAsStream("/splunk-crds.yaml"));
                resources.delete();
                resources.create();
            }

            String operatorResources = Resources.toString(this.getClass().getResource("/splunk-operator-namespace.yaml"), StandardCharsets.UTF_8)
                    .replace("DESIRED_NAMESPACE", OpenshiftClient.get().getNamespace()).replace("SPLUNK_IMAGE", image());
            InputStream is = IOUtils.toInputStream(operatorResources, "UTF-8");

            LOG.info("Creating Splunk openshift resources from splunk-operator-namespace.yaml");
            createdResources = OpenshiftClient.get().load(is).serverSideApply();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read splunk-operator-namespace.yml or splunk-crds.yaml resource: ", e);
        }

        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).resource(customResource()).serverSideApply();
    }

    @Override
    public void undeploy() {
        LOG.info("Undeploying Splunk resources");
        OpenshiftClient.get().genericKubernetesResources(apiVersion(), kind()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
        OpenshiftClient.get().resourceList(createdResources.stream().filter(res -> !(res instanceof CustomResourceDefinition
                || res instanceof io.fabric8.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinitionVersion
                || res instanceof ClusterRole))
            .collect(Collectors.toList())).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();

        //if CR creates PVC's, they need to be deleted. (usage of the finalizer in CR can in some situations (e.g. failure during operator
        // creation) cause the deletion of CR get stuck)
        OpenshiftClient.get().persistentVolumeClaims().withLabel("app.kubernetes.io/name", "standalone").delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areNoPodsPresent("name", "splunk-operator").timeout(120_000).waitFor();
    }

    @Override
    public void openResources() {
        if (getConfiguration().getProtocol().equals(SplunkProtocol.HTTPS)) {
            String splunkCert = OpenshiftClient.get().podShell(OpenshiftClient.get().getAnyPod("app.kubernetes.io/instance",
                    "splunk-" + name() + "-standalone")).execute("cat", "/opt/splunk/etc/auth/cacert.pem").getOutput();
            apiRoute = OpenshiftClient.get().routes().createOrReplace(new RouteBuilder()
                .editOrNewMetadata()
                .withName(ROUTE_NAME)
                .endMetadata()
                .editOrNewSpec()
                .withNewTo().withKind("Service").withName(serviceName).withWeight(100)
                .endTo()
                .withNewPort().withTargetPort(new IntOrString(SERVICE_API_PORT)).endPort()
                .withNewTls().withTermination("reencrypt").withDestinationCACertificate(splunkCert).endTls()
                .endSpec()
                .build());
        } else if (getConfiguration().getProtocol().equals(SplunkProtocol.HTTP)) {
            apiRoute = OpenshiftClient.get().routes().createOrReplace(new RouteBuilder()
                .editOrNewMetadata()
                .withName(ROUTE_NAME)
                .endMetadata()
                .editOrNewSpec()
                .withNewTo().withKind("Service").withName(serviceName).withWeight(100)
                .endTo()
                .withNewPort().withTargetPort(new IntOrString(SERVICE_API_PORT)).endPort()
                .endSpec()
                .build());
        }
        WaitUtils.waitFor(() -> HTTPUtils.getInstance().get(getConfiguration().getProtocol().toString() + "://"
            + apiRoute.getSpec().getHost(), false).isSuccessful(), "Waiting until the Splunk API route is ready");
    }

    @Override
    public void restart() {
        OpenshiftDeployable.super.restart();
    }

    @Override
    public void closeResources() {
        if (apiRoute != null) {
            OpenshiftClient.get().routes().resource(apiRoute).delete();
        }
        validation = null;
        client = null;
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("Ansible playbook complete");
    }

    @Override
    public boolean isDeployed() {
        List<Pod> pods = OpenshiftClient.get().pods().withLabel("name", "splunk-operator").list().getItems();
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0));
    }

    @Override
    public String name() {
        return "s1";
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("app.kubernetes.io/instance", "splunk-" + name() + "-standalone"));
    }

    @Override
    public String externalHostname() {
        return apiRoute.getSpec().getHost();
    }

    @Override
    public SplunkAccount account() {
        if (account == null) {
            account = AccountFactory.create(SplunkAccount.class);
            account.setPassword(new String(Base64.getDecoder().decode(
                OpenshiftClient.get().getSecret("splunk-" + name() + "-standalone-secret-v1").getData().get("password"))));
        }
        return account;
    }

    private CustomResourceDefinition getSplunkCrd() {
        return OpenshiftClient.get().apiextensions().v1().customResourceDefinitions().withName("standalones.enterprise.splunk.com").get();
    }

    @Override
    public int apiPort() {
        return getConfiguration().getProtocol().equals(SplunkProtocol.HTTPS) ? 443 : 80;
    }

    @Override
    public String kind() {
        return "Standalone";
    }

    @Override
    public String apiVersion() {
        return "enterprise.splunk.com/" + CRD_API;
    }

    @Override
    public GenericKubernetesResource customResource() {
        Map<String, Object> spec = new HashMap<>(Map.of(
            "varVolumeStorageConfig", Map.of(
                "ephemeralStorage", true
            ),
            "etcVolumeStorageConfig", Map.of(
                "ephemeralStorage", true
            )
        ));

        if (SplunkProtocol.HTTP == getConfiguration().getProtocol()) {
            spec.put("extraEnv", List.of(Map.of("name", "SPLUNKD_SSL_ENABLE", "value", "false")));
        }

        return new GenericKubernetesResourceBuilder()
            .withApiVersion(apiVersion())
            .withKind(kind())
            .withNewMetadata()
            .withName(name())
            .endMetadata()
            .addToAdditionalProperties("spec", spec)
            .build();
    }
}
