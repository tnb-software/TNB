package software.tnb.product.integration.configuration;

public record VolumeMountConfig(String volumeName, String mountPath, boolean readOnly) {
}
