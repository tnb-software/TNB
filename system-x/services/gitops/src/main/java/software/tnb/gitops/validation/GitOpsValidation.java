//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package software.tnb.gitops.validation;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.validation.Validation;
import software.tnb.gitops.service.ArgoClient;
import software.tnb.gitops.service.GitOps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GitOpsValidation implements Validation {
    private ArgoClient client;

    public GitOpsValidation(ArgoClient client) {
        this.client = client;
    }

    public void createApplication(String name, String repo, String path) {
        this.client.createApp(name, OpenshiftClient.get().getNamespace(), repo, path, GitOps.ARGO_PROJECT_NAME, Collections.EMPTY_LIST);
    }

    public void createApplication(String name, String repo, String path, List<String> imageOverrides) {
        this.client.createApp(name, OpenshiftClient.get().getNamespace(), repo, path, GitOps.ARGO_PROJECT_NAME, imageOverrides);
    }

    public void deleteApplication(String name) {
        this.client.deleteApp(name);
    }

    public void configureRepository(String name, String url, boolean insecure) {
        this.client.configureRepo(name, url, insecure, GitOps.ARGO_PROJECT_NAME);
    }

    public void syncApp(String name, String revision) throws IOException {
        this.client.syncApp(name, revision);
    }
}
