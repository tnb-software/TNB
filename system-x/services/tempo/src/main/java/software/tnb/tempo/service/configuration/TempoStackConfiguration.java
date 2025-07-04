package software.tnb.tempo.service.configuration;

import software.tnb.aws.s3.service.Minio;
import software.tnb.common.service.configuration.ServiceConfiguration;

import java.util.Map;

public class TempoStackConfiguration extends ServiceConfiguration {

    private static final String RESOURCE_LIMITS_CPU = "tempo.resource.limits.cpu";
    private static final String RESOURCE_LIMITS_MEMORY = "tempo.resource.limits.memory";
    private static final String STORAGE_TYPE = "tempo.storage.type";
    private static final String STORAGE_SECRET = "tempo.storage.secret";
    private static final String STORAGE_SECRET_KEYS = "tempo.storage.secret.keys";
    private static final String STORAGE_SIZE = "tempo.storage.size";

    private Minio minioStorage;

    public TempoStackConfiguration withResourceLimitsCpu(String cpu) {
        set(RESOURCE_LIMITS_CPU, cpu);
        return this;
    }

    public String getResourceLimitsCpu() {
        return get(RESOURCE_LIMITS_CPU, String.class);
    }

    public TempoStackConfiguration withResourceLimitsMemory(String memory) {
        set(RESOURCE_LIMITS_MEMORY, memory);
        return this;
    }

    public String getResourceLimitsMemory() {
        return get(RESOURCE_LIMITS_MEMORY, String.class);
    }

    public TempoStackConfiguration withStorageType(String storageType) {
        set(STORAGE_TYPE, storageType);
        return this;
    }

    public String getStorageType() {
        return get(STORAGE_TYPE, String.class);
    }

    public TempoStackConfiguration withStorageSecret(String storageSecret) {
        set(STORAGE_SECRET, storageSecret);
        return this;
    }

    public String getStorageSecret() {
        return get(STORAGE_SECRET, String.class);
    }

    public TempoStackConfiguration withStorageSize(String storageSize) {
        set(STORAGE_SIZE, storageSize);
        return this;
    }

    public String getStorageSize() {
        return get(STORAGE_SIZE, String.class);
    }

    public TempoStackConfiguration createStorageSecret(String secretName, Minio minio, String bucket) {
        set(STORAGE_SECRET_KEYS, Map.of("name", secretName
            , "access_key_id", minio.account().accessKey()
            , "access_key_secret", minio.account().secretKey()
            , "endpoint", minio.hostname()
            , "bucket", bucket));
        minioStorage = minio;
        return withStorageSecret(secretName);
    }

    public Map<String, String> getStorageSecretKeys() {
        return get(STORAGE_SECRET_KEYS, Map.class);
    }

    public Minio getMinioStorage() {
        return minioStorage;
    }
}
