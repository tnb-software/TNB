package software.tnb.splunk.resource.openshift;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.splunk.account.SplunkAccount;
import software.tnb.splunk.service.Splunk;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.rbac.ClusterRole;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(Splunk.class)
public class OpenshiftSplunk extends Splunk implements ReusableOpenshiftDeployable {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftSplunk.class);
    public static final String ROUTE_NAME = "api";
    public static final int API_PORT = 443;
    private static List<HasMetadata> createdResources;
    private CustomResourceDefinitionContext crdContext;
    private Route apiRoute;

    private String sccName;

    @Override
    public void create() {
        LOG.info("Deploying OpenShift Splunk");
        sccName = "tnb-splunk-" + OpenshiftClient.get().getNamespace();

        OpenshiftClient.get().addUsersToSecurityContext(
            OpenshiftClient.get().createSecurityContext(sccName, "nonroot"),
            OpenshiftClient.get().getServiceAccountRef("splunk-operator-controller-manager"),
            OpenshiftClient.get().getServiceAccountRef("default"));

        try {
            if (getSplunkCrd() == null) { // test if CRD already exists on cluster
                LOG.info("Creating Splunk CRD's from splunk-crds.yaml");
                OpenshiftClient.get().load(this.getClass().getResourceAsStream("/splunk-crds.yaml")).createOrReplace();
            }
            String resources =
                Resources.toString(Objects.requireNonNull(this.getClass().getResource("/splunk-operator-namespace.yaml")), StandardCharsets.UTF_8)
                    .replace("DESIRED_NAMESPACE", OpenshiftClient.get().getNamespace());
            InputStream is = IOUtils.toInputStream(resources, "UTF-8");

            LOG.info("Creating Splunk openshift resources from splunk-operator-namespace.yaml");
            createdResources = OpenshiftClient.get().load(is).createOrReplace();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read splunk-operator-namespace.yml or splunk-crds.yaml resource: ", e);
        }

        Map<String, Object> splunkInstance = Map.of(
            "apiVersion", "enterprise.splunk.com/v3",
            "kind", "Standalone",
            "metadata", Map.of(
                "name", "s1")
        );
        try {
            OpenshiftClient.get().customResource(createSplunkContext()).inNamespace(OpenshiftClient.get().getNamespace()).delete();
            OpenshiftClient.get().customResource(createSplunkContext()).inNamespace(OpenshiftClient.get().getNamespace())
                .create(splunkInstance);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create Splunk CR: ", e);
        }
    }

    @Override
    public void undeploy() {
        LOG.info("Undeploying Splunk resources");
        OpenshiftClient.get().customResource(createSplunkContext()).inNamespace(OpenshiftClient.get().getNamespace()).delete();
        WaitUtils.waitFor(() -> !this.isAppDeployed(), "Waiting until Splunk CR is uninstalled.");
        OpenshiftClient.get().resourceList(createdResources.stream()
            .filter(res -> !(res instanceof CustomResourceDefinition
                || res instanceof io.fabric8.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinitionVersion
                || res instanceof ClusterRole))
            .collect(Collectors.toList())).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();

        //if CR creates PVC's, they need to be deleted. (usage of the finalizer in CR can in some situations (e.g. failure during operator
        // creation) cause the deletion of CR get stuck)
        OpenshiftClient.get().persistentVolumeClaims().withLabel("app.kubernetes.io/name", "standalone").delete();
    }

    @Override
    public void openResources() {
        String splunkCert = OpenshiftClient.get().podShell(OpenshiftClient.get().getAnyPod("app.kubernetes.io/instance", "splunk-s1-standalone"))
            .execute("cat", "/opt/splunk/etc/auth/cacert.pem").getOutput();

        // @formatter:off
        apiRoute = OpenshiftClient.get().routes().createOrReplace(new RouteBuilder()
            .editOrNewMetadata()
                .withName(ROUTE_NAME)
            .endMetadata()
            .editOrNewSpec()
                .withNewTo().withKind("Service").withName("splunk-s1-standalone-service").withWeight(100)
                .endTo()
                .withNewPort().withTargetPort(new IntOrString("https-splunkd")).endPort()
                .withNewTls().withTermination("reencrypt").withDestinationCACertificate(splunkCert).endTls()
            .endSpec()
            .build());
        // @formatter:on
        WaitUtils.waitFor(() -> HTTPUtils.getInstance().get("https://" + apiRoute.getSpec().getHost()).isSuccessful(),
            "Waiting until the Splunk API route is ready");
    }

    @Override
    public void closeResources() {
        if (apiRoute != null) {
            OpenshiftClient.get().routes().delete(apiRoute);
        }
    }

    @Override
    public boolean isReady() {
        return isAppDeployed()
            && OpenshiftClient.get().getLogs(OpenshiftClient.get().getAnyPod("app.kubernetes.io/instance", "splunk-s1-standalone"))
            .contains("Ansible playbook complete");
    }

    @Override
    public boolean isDeployed() {
        return isOperatorDeployedAndReady() && isAppDeployed();
    }

    private boolean isOperatorDeployedAndReady() {
        List<Pod> pods = OpenshiftClient.get().pods().withLabel("name", "splunk-operator").list().getItems();
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0));
    }

    private boolean isAppDeployed() {
        List<Pod> pods = OpenshiftClient.get().pods().withLabel("app.kubernetes.io/instance", "splunk-s1-standalone").list().getItems();
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0));
    }

    @Override
    public void cleanup() {
        for (String customIndex : client().getIndexes().keySet()) {
            try {
                client().getIndexes().remove(customIndex);
            } catch (com.splunk.HttpException ex) { // ignore when internal or disabled indexes are not deleted.
                if (!(ex.getDetail().contains("is internal") || ex.getDetail().contains("is disabled"))) {
                    throw ex;
                }
            }
        }
    }

    @Override
    public String externalHostname() {
        return apiRoute.getSpec().getHost();
    }

    @Override
    public SplunkAccount account() {
        if (account == null) {
            account = AccountFactory.create(SplunkAccount.class);
            account.setPassword(
                new String(Base64.getDecoder().decode(OpenshiftClient.get().getSecret("splunk-s1-standalone-secret-v1").getData().get("password"))));
        }
        return account;
    }

    @Override
    public String apiSchema() {
        return "https";
    }

    private CustomResourceDefinition getSplunkCrd() {
        return OpenshiftClient.get().apiextensions().v1().customResourceDefinitions().withName("standalones.enterprise.splunk.com").get();
    }

    private CustomResourceDefinitionContext createSplunkContext() {
        if (crdContext == null) {
            CustomResourceDefinition crd = getSplunkCrd();
            CustomResourceDefinitionContext.Builder builder = new CustomResourceDefinitionContext.Builder()
                .withGroup(crd.getSpec().getGroup())
                .withPlural(crd.getSpec().getNames().getPlural())
                .withScope(crd.getSpec().getScope())
                .withVersion("v3");
            crdContext = builder.build();
        }
        return crdContext;
    }

    @Override
    public int apiPort() {
        return API_PORT;
    }
}
