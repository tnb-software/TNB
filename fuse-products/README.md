# TNB - Fuse Products

This module contains the logic for working with supported fuse products on both `local machine` and `OpenShift`.

For each product there are two main areas covered:

- deployment - deploying and undeploying product (where applicable)
- integrations - creating, starting, stopping of integrations

The integration code is generated from a "meta" [Integration Builder](src/main/java/org/jboss/fuse/tnb/product/integration/builder/AbstractIntegrationBuilder.java)
class and 0..x [Customizer](src/main/java/org/jboss/fuse/tnb/product/customizer/Customizer.java)s for given system-x services using
the [javaparser](https://javaparser.org/) framework. See [RouteBuilders guide](RouteBuilders.md) for more details.

There are several integration builder classes to use dependending on the use-case:
- [AbstractIntegrationBuilder](src/main/java/org/jboss/fuse/tnb/product/integration/builder/AbstractIntegrationBuilder.java) serves as a base
for creating integrations on all products (so there are methods related to every product only)
  - it is possible to instantiate it via [IntegrationBuilder](src/main/java/org/jboss/fuse/tnb/product/integration/builder/IntegrationBuilder.java) class
- AbstractGitIntegrationBuilder
- AbstractMavenGitIntegrationBuilder
- [CamelKIntegrationBuilder](src/main/java/org/jboss/fuse/tnb/product/ck/integration/builder/CamelKIntegrationBuilder.java)
that extends `AbstractIntegrationBuilder` and adds methods related to camel-k only
- [SpringBootIntegrationBuilder](src/main/java/org/jboss/fuse/tnb/product/csb/integration/builder/SpringBootIntegrationBuilder.java)
  that extends `AbstractIntegrationBuilder` and adds methods related to camel on springboot only

Customizers are used when the integration should run on all products, but the configuration differs between products. In that case, you need to use
a customizer, where you have access to the IntegrationBuilder and all its methods.

Again, there are multiple customizers you can use:
- [ProductsCustomizer](src/main/java/org/jboss/fuse/tnb/product/customizer/ProductsCustomizer.java) - when you want to do modifications for two
or more products
- [SpringBootCustomizer](src/main/java/org/jboss/fuse/tnb/product/csb/customizer/SpringbootCustomizer.java),
[QuarkusCustomizer](src/main/java/org/jboss/fuse/tnb/product/cq/customizer/QuarkusCustomizer.java),
[CamelKCustomizer](src/main/java/org/jboss/fuse/tnb/product/ck/customizer/CamelKCustomizer.java) to do the change only for specific product

Instead of creating `new SpringBoot|Quarkus|CamelK customizers`, you can use
[Customizers](src/main/java/org/jboss/fuse/tnb/product/customizer/Customizers.java) enum, for example:

```java
Customizers.CAMELK.customize(ib -> ...)
```

There are also customizer implementations for common modifications needed for a given product. You can check them out in `customizer` sub-package
inside the product's package.


The integrations are created differently for each product:

- `camel on springboot`:
    - an application skeleton is generated from the [archetype](https://github.com/tnb-software/camel3-archetype-spring-boot)
    - the `integration code` is dumped as a `java file` in the app skeleton
- `camel quarkus`:
    - an application skeleton is generated from the `io.quarkus:quarkus-maven-plugin:<version>:create` maven plugin
    - the `integration code` is dumped as a `java file` in the app skeleton
- `camel-k`:
    - the `integration code` is dumped as a `String` and the integration is created as the `Integration` object in OpenShift

All products are implementing [JUnit 5 extensions](https://junit.org/junit5/docs/current/user-guide/#extensions) so creating a fuse product in your
test is as simple as adding following piece of code:

```java
@RegisterExtension
public static Product product = ProductFactory.create();
```

In this case a correct product instance is determined based on system property `fuse.product` (camelspringboot, camelquarkus, camelk)
and based on `openshift.url` property presence (determines if the deployment is local or openshift)

If you want a specific instance of a given fuse product, you can use:

```java
@RegisterExtension
public static CamelK camelk = ProductFactory.create(CamelK.class);
```

for example to test features specific to Camel-K only.
