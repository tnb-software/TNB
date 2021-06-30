package org.jboss.fuse.tnb.customizer.mongodb;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.integration.Customizer;

import com.github.javaparser.StaticJavaParser;

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

    private void customizeStandalone() {
        /*
            bindToRegistry("<name>", MongoClients.create("<replicaSetUrl>");
         */
        final String statement = String.format("bindToRegistry(\"%s\", MongoClients.create(\"%s\"))", name, replicaSetUrl);
        getConfigureMethod().getBody().get().addStatement(0, StaticJavaParser.parseExpression(statement));
        getRouteBuilder().addImport("com.mongodb.client.MongoClients");
    }

    private void customizeQuarkus() {
        getApplicationProperties().put("quarkus.mongodb.connection-string", replicaSetUrl);
    }
}
