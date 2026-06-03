package software.tnb.product.quarkus.cxf;

import software.tnb.product.Product;
import software.tnb.product.quarkus.cxf.variant.CxfQuarkusVariant;
import software.tnb.product.quarkus.vanilla.LocalQuarkus;
import software.tnb.product.quarkus.vanilla.variant.QuarkusVariant;

import com.google.auto.service.AutoService;

@AutoService(Product.class)
public class LocalCxfQuarkus extends LocalQuarkus {

    @Override
    protected QuarkusVariant quarkusVariant() {
        return new CxfQuarkusVariant();
    }

}
