package software.tnb.gitops.service;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;

import java.util.List;
import java.util.Map;

import generated.io.argoproj.v1alpha1.Application;
import generated.io.argoproj.v1alpha1.ApplicationSpec;
import generated.io.argoproj.v1alpha1.applicationspec.Destination;
import generated.io.argoproj.v1alpha1.applicationspec.Source;
import generated.io.argoproj.v1alpha1.applicationspec.source.Kustomize;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;

public class ArgoClient {
    public ArgoClient() {
    }

    public void configureRepo(String name, String url, boolean insecure, String project) {
        Secret repo = new SecretBuilder().withStringData(
                Map.of("insecure", String.valueOf(insecure), "type", "https", "url", url, project, project)).withNewMetadata()
            .withName(name).withAnnotations(Map.of("managed-by", "argocd.argoproj.io")).withLabels(
                Map.of("argocd.argoproj.io/secret-type", "repository")).endMetadata().build();
        OpenshiftClient.get().secrets().inNamespace("openshift-gitops").create(repo);
    }

    public Application createApp(String name, String namespace, String repo, String path, String project, List<String> imageOverrides) {
        Application app = new Application();
        app.getMetadata().setName(name);
        ApplicationSpec spec = new ApplicationSpec();
        spec.setProject(project);
        app.setSpec(spec);
        Source src = new Source();
        src.setRepoURL(repo);
        src.setPath(path);
        Kustomize k = new Kustomize();
        k.setImages(imageOverrides);
        src.setKustomize(k);
        spec.setSource(src);
        Destination dest = new Destination();
        dest.setServer("https://kubernetes.default.svc");
        dest.setNamespace(namespace);
        spec.setDestination(dest);
        return OpenshiftClient.get().resources(Application.class).inNamespace("openshift-gitops").resource(app).create();
    }

    public void deleteApp(String name) {
        OpenshiftClient.get().resources(Application.class).inNamespace(GitOps.GITOPS_NAMESPACE).withName(name).delete();
        WaitUtils.waitFor(
            () -> OpenshiftClient.get().resources(Application.class).inNamespace(GitOps.GITOPS_NAMESPACE).withName(name).get() == null,
            "Wait for Argo app deletion.");
    }

    public void syncApp(String name, String revision) {
        final GenericKubernetesResource app =
            OpenshiftClient.get().genericKubernetesResources("argoproj.io/v1alpha1", "Application").inNamespace("openshift-gitops")
                .withName(name).get();
        app.getAdditionalProperties().put("revision", revision);
        OpenshiftClient.get().genericKubernetesResources("argoproj.io/v1alpha1", "Application").inNamespace("openshift-gitops")
            .resource(app).createOrReplace();
    }
}
