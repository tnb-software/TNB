package org.jboss.fuse.tnb.customizer.mongodb;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.customizer.Customizer;

import com.squareup.javapoet.ClassName;

/**
 * Customizer for MongoDB that creates the bean with mongodb client.
 */
public class MongoDBCustomizer extends Customizer {
    private final String name;
    private final String replicaSetUrl;

    public MongoDBCustomizer(String name, String replicaSetUrl) {
        this.name = name;
        this.replicaSetUrl = replicaSetUrl;
    }

    @Override
    public void customize() {
        if (TestConfiguration.product() == ProductType.CAMEL_STANDALONE) {
            customizeStandalone();
        } else {
            customizeQuarkus();
        }
    }

    private void customizeQuarkus() {
        getApplicationProperties().put("quarkus.mongodb.connection-string", replicaSetUrl);
    }

    private void customizeStandalone() {
        /*
            bindToRegistry("<name>", MongoClients.create("<replicaSetUrl>");
         */
        getConfigureMethodBuilder().addStatement("bindToRegistry($S, $T.create($S))",
            name, ClassName.get("com.mongodb.client", "MongoClients"), replicaSetUrl);
    }
}
