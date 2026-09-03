package software.tnb.weaviate.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;

import java.util.Map;

public abstract class Weaviate extends Service<NoAccount, NoClient, NoValidation> implements WithDockerImage {

    protected static final int PORT = 8080;
    protected static final int GRPC_PORT = 50051;
    public static final Map<String, String> WEAVIATE_ENV = Map.of(
        "AUTHENTICATION_ANONYMOUS_ACCESS_ENABLED", "true",
        "DEFAULT_VECTORIZER_MODULE", "none",
        "PERSISTENCE_DATA_PATH", "/var/lib/weaviate"
    );

    public abstract String host();

    public abstract int port();

    public abstract int grpcPort();

    public String url() {
        return String.format("http://%s:%d", host(), port());
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/weaviate:1.37.4";
    }
}
