package software.tnb.product.junit.product;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import software.tnb.common.product.ProductType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE, METHOD})
@Retention(RUNTIME)
@RunOn(product = ProductType.CAMEL_K, local = false)
public @interface CamelKOnly {
}
