package software.tnb.product.endpoint;

import java.util.function.Supplier;

public class Endpoint {

    private String endpoint;
    private Supplier<String> endpointSupplier;

    public Endpoint(Supplier<String> endpointSupplier) {
        this.endpoint = null;
        this.endpointSupplier = endpointSupplier;
    }

    public String getAddress() {
        if (endpoint == null) {
            endpoint = endpointSupplier.get();
        }

        return endpoint;
    }
}
