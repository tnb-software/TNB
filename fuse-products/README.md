# TNB - Fuse Products

This module contains the logic for working with supported fuse products on both `local machine` and `OpenShift`.

For each product there are two main areas covered:

- deployment - deploying and undeploying product (where applicable)
- integrations - creating, starting, stopping of integrations

The integration code is generated from a "meta"
class [IntegrationBuilder](src/main/java/org/jboss/fuse/tnb/product/integration/IntegrationBuilder.java)
and 0..x [Customizer](../system-x/customizers/src/main/java/org/jboss/fuse/tnb/customizer/Customizer.java)s for given system-x services using
the [javaparser](https://javaparser.org/) framework. See [RouteBuilders guide](RouteBuilders.md) for more details.

The integrations are created differently for each product:

- `camel standalone`:
    - an application skeleton is generated from the `org.apache.camel.archetypes:camel-archetype-main` archetype
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

In this case a correct product instance is determined based on system property `fuse.product` (camelstandalone, camelquarkus, camelk)
and based on `openshift.url` property presence (determines if the deployment is local or openshift)

If you want a specific instance of a given fuse product, you can use:

```java
@RegisterExtension
public static CamelK camelk = ProductFactory.create(CamelK.class);
```

for example to test features specific to Camel-K only.
