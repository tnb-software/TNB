package software.tnb.gitops.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.service.Service;
import software.tnb.common.utils.WaitUtils;
import software.tnb.gitops.validation.GitOpsValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import generated.io.argoproj.v1alpha1.AppProject;
import generated.io.argoproj.v1alpha1.AppProjectSpec;
import generated.io.argoproj.v1alpha1.appprojectspec.ClusterResourceWhitelist;
import generated.io.argoproj.v1alpha1.appprojectspec.Destinations;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.Pod;

@AutoService(GitOps.class)
public class GitOps extends Service<NoAccount, ArgoClient, GitOpsValidation> implements OpenshiftDeployable, WithOperatorHub {
    private static final Logger LOG = LoggerFactory.getLogger(GitOps.class);
    public static final String ARGO_PROJECT_NAME = "tnb-" + UUID.randomUUID();
    public static final String GITOPS_NAMESPACE = "openshift-gitops";
    private final ArgoClient client = new ArgoClient();

    public GitOps() {
    }

    private AppProject createProject(String projectName) {
        LOG.debug("Create Argo project {}", ARGO_PROJECT_NAME);
        AppProject project = new AppProject();
        project.getMetadata().setName(projectName);
        project.setSpec(new AppProjectSpec());
        ClusterResourceWhitelist whitelist = new ClusterResourceWhitelist();
        whitelist.setGroup("*");
        whitelist.setKind("*");
        Destinations dest = new Destinations();
        dest.setNamespace("*");
        dest.setServer("*");
        project.getSpec().setClusterResourceWhitelist(List.of(whitelist));
        project.getSpec().setDestinations(List.of(dest));
        project.getSpec().setSourceRepos(List.of("*"));
        return OpenshiftClient.get().resources(AppProject.class).inNamespace(GITOPS_NAMESPACE).resource(project).create();
    }

    public GitOpsValidation validation() {
        if (validation == null) {
            validation = new GitOpsValidation(client);
        }

        return validation;
    }

    public void undeploy() {
        LOG.debug("Undeploy GitOps operator");
        deleteSubscription(
            () -> OpenshiftClient.get().pods().inNamespace(targetNamespace()).withName("gitops-operator-controller-manager").get() == null);
    }

    public void openResources() {
        // no-op
    }

    public void closeResources() {
        OpenshiftClient.get().deleteProject(GITOPS_NAMESPACE);
        WaitUtils.waitFor(() -> OpenshiftClient.get().getProject(GITOPS_NAMESPACE) == null,
            String.format("Removing %s project", GITOPS_NAMESPACE));
    }

    public void create() {
        LOG.debug("Creating GitOps operator");
        createSubscription();
        WaitUtils.waitFor(() -> OpenshiftClient.get().namespaces().withName(GITOPS_NAMESPACE).get() != null,
            "Waiting until the gitops namespace is created");
        createProject(ARGO_PROJECT_NAME);

        // edit namespace since the project resource is immutable
        OpenshiftClient.get().namespaces().withName(OpenshiftClient.get().getProject().getMetadata().getName()).edit((p) -> {
            p.getMetadata().getLabels().put("argocd.argoproj.io/managed-by", GITOPS_NAMESPACE);
            return p;
        });
    }

    public boolean isReady() {
        final GenericKubernetesResource argoCD =
            OpenshiftClient.get().genericKubernetesResources("argoproj.io/v1alpha1", "ArgoCD").inNamespace(GITOPS_NAMESPACE)
                .withName("openshift-gitops").get();
        return argoCD != null && argoCD.getAdditionalProperties().get("status") != null
            && ((Map) argoCD.get("status")).get("phase").equals("Available");
    }

    public boolean isDeployed() {
        return OpenshiftClient.get().pods().inNamespace(targetNamespace()).withName("gitops-operator-controller-manager").get() != null;
    }

    public Predicate<Pod> podSelector() {
        return null;
    }

    public void restart() {
    }

    public String targetNamespace() {
        return "openshift-operators";
    }

    public String operatorName() {
        return "openshift-gitops-operator";
    }

    public String operatorChannel() {
        return "latest";
    }

    public boolean clusterWide() {
        return true;
    }
}
