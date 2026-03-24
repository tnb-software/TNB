package software.tnb.qdrant.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.qdrant.validation.QdrantValidation;

public abstract class Qdrant extends Service<NoAccount, NoClient, QdrantValidation> implements WithDockerImage {

    protected static final int PORT = 6333;

    public abstract String host();

    public abstract int port();

    public String url() {
        return String.format("http://%s:%d", host(), port());
    }

    public QdrantValidation validation() {
        if (validation == null) {
            validation = new QdrantValidation(url());
        }
        return validation;
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/qdrant:1.16.3";
    }
}
