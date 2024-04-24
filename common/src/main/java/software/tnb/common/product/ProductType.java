package software.tnb.common.product;

public enum ProductType {
    CAMEL_QUARKUS("camelquarkus"),
    CAMEL_K("camelk"),
    CAMEL_SPRINGBOOT("camelspringboot"),
    CXF_QUARKUS("cxfquarkus");

    private final String value;

    ProductType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
