package software.tnb.product.quarkus.camel;

import software.tnb.product.Product;
import software.tnb.product.quarkus.camel.variant.CamelQuarkusVariant;
import software.tnb.product.quarkus.vanilla.OpenshiftQuarkus;
import software.tnb.product.quarkus.vanilla.variant.QuarkusVariant;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class OpenshiftCamelQuarkus extends OpenshiftQuarkus {

    @Override
    protected QuarkusVariant quarkusVariant() {
        return new CamelQuarkusVariant();
    }
}
