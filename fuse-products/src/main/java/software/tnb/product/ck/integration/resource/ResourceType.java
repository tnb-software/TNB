package software.tnb.product.ck.integration.resource;

public enum ResourceType {
    FILE("file"),
    CONFIG_MAP("configmap"),
    SECRET("secret");

    private final String value;

    ResourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
