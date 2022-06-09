package software.tnb.common.product;

public enum ProductType {
    CAMEL_QUARKUS("camelquarkus"),
    CAMEL_K("camelk"),
    CAMEL_SPRINGBOOT("camelspringboot");

    private final String value;

    ProductType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
