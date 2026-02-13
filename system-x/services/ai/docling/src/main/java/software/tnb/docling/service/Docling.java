package software.tnb.docling.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.docling.validation.DoclingValidation;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public abstract class Docling extends Service<NoAccount, CloseableHttpClient, DoclingValidation> implements WithDockerImage {

    protected static final int PORT = 5001;

    public abstract String host();

    public abstract int port();

    public String url() {
        return String.format("http://%s:%d", host(), port());
    }

    public DoclingValidation validation() {
        if (validation == null) {
            validation = new DoclingValidation(client(), url());
        }
        return validation;
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/docling-serve:1.12.0";
    }
}
