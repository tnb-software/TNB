package org.jboss.fuse.tnb.product.deploystrategy;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.jboss.fuse.tnb.common.product.ProductType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface DeployableWith {
    OpenshiftDeployStrategy strategy();
    ProductType[] product() default {};
}
