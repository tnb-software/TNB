package org.jboss.fuse.tnb.product.junit.product;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.jboss.fuse.tnb.common.product.ProductType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE, METHOD})
@Retention(RUNTIME)
@RunOn(product = ProductType.CAMEL_SPRINGBOOT, openshift = false)
public @interface SpringBootOnly {
}
