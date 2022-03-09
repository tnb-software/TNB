package org.jboss.fuse.tnb.product.ck.integration.resource;

public enum ResourceType {
    DATA("data"),
    CONFIG("config"),
    OPENAPI("openapi");

    private final String value;

    ResourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
