package software.tnb.ollama.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.ollama.validation.OllamaValidation;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public abstract class Ollama extends Service<NoAccount, CloseableHttpClient, OllamaValidation> implements WithDockerImage {

    protected static final int PORT = 11434;

    public abstract String host();

    public abstract int port();

    public String url() {
        return String.format("http://%s:%d", host(), port());
    }

    public OllamaValidation validation() {
        if (validation == null) {
            validation = new OllamaValidation(client(), url());
        }
        return validation;
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/ollama:0.12.11";
    }
}
