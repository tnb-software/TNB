package org.jboss.fuse.tnb.product.integration;

public enum ResourceType {
    DATA("data"),
    CONFIG("config"),
    OPENAPI("openapi"),
    XML_CAMEL_CONTEXT("xmlCamelContext");

    private final String value;

    ResourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
