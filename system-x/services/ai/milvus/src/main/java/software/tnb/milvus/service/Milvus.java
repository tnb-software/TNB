package software.tnb.milvus.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.milvus.validation.MilvusValidation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class Milvus extends Service<NoAccount, NoClient, MilvusValidation> implements WithDockerImage {

    protected static final int PORT = 19530;
    private static final String ETCD_CONFIG_RESOURCE = "/embedEtcd.yaml";

    public static final Map<String, String> MILVUS_ENV = Map.of(
        "ETCD_USE_EMBED", "true",
        "ETCD_DATA_DIR", "/var/lib/milvus/etcd",
        "ETCD_CONFIG_PATH", "/milvus/configs/embedEtcd.yaml",
        "COMMON_STORAGETYPE", "local",
        "DEPLOY_MODE", "STANDALONE"
    );

    public abstract String host();

    public abstract int port();

    public String url() {
        return String.format("http://%s:%d", host(), port());
    }

    public MilvusValidation validation() {
        if (validation == null) {
            validation = new MilvusValidation(url());
        }
        return validation;
    }

    public static String embedEtcdConfig() {
        try (InputStream is = Milvus.class.getResourceAsStream(ETCD_CONFIG_RESOURCE)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + ETCD_CONFIG_RESOURCE + " resource", e);
        }
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/milvus:2.6.11";
    }
}
