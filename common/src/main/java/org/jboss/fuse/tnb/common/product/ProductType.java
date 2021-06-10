package org.jboss.fuse.tnb.common.product;

public enum ProductType {
    CAMEL_STANDALONE("camelstandalone"),
    CAMEL_QUARKUS("camelquarkus"),
    CAMEL_K("camelk");

    private final String value;

    ProductType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
