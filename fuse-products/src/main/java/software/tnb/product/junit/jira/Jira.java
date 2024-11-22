package software.tnb.product.junit.jira;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for Jira issues.
 * <p>
 * For each jira issue, you can specify the product configuration (environment) the jira belongs to (if the jira is reported for one env only), if
 * not specified, defaults to all configurations
 * <p>
 * You can use multiple jira annotations on one class / method:
 * \@Jira(configuration = ProductConfiguration.QUARKUS_NATIVE, keys = {"ENTESB-1", "ENTESB-2"})
 * \@Jira(configuration = ProductConfiguration.CAMEL_K, keys = {"ENTESB-3", "ENTESB-4"})
 * <p>
 * All jira annotations for given class / method are then stored automatically in {@link Jiras} annotation
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Repeatable(Jiras.class)
public @interface Jira {
    ProductConfiguration configuration() default ProductConfiguration.ALL;

    String[] keys();
}
