package software.tnb.product.integration.configuration;

public final class VolumeConfig {

    public enum VolumeType {
        CONFIG_MAP("configMap"),
        SECRET("secret"),
        EMPTY_DIR("emptyDir");

        private final String value;

        VolumeType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final String name;
    private final VolumeType type;
    private final String source; // null for emptyDir

    private VolumeConfig(String name, VolumeType type, String source) {
        this.name = name;
        this.type = type;
        this.source = source;
    }

    public static VolumeConfig configMap(String name, String configMapName) {
        if (configMapName == null || configMapName.isEmpty()) {
            throw new IllegalArgumentException("ConfigMap name cannot be null or empty");
        }
        return new VolumeConfig(name, VolumeType.CONFIG_MAP, configMapName);
    }

    public static VolumeConfig secret(String name, String secretName) {
        if (secretName == null || secretName.isEmpty()) {
            throw new IllegalArgumentException("Secret name cannot be null or empty");
        }
        return new VolumeConfig(name, VolumeType.SECRET, secretName);
    }

    public static VolumeConfig emptyDir(String name) {
        return new VolumeConfig(name, VolumeType.EMPTY_DIR, null);
    }

    public String name() {
        return name;
    }

    public VolumeType type() {
        return type;
    }

    public String typeString() {
        return type.getValue();
    }

    public String source() {
        return source;
    }
}
