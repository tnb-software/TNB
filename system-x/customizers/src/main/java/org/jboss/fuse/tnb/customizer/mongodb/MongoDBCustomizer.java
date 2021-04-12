package org.jboss.fuse.tnb.customizer.mongodb;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.customizer.Customizer;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;

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
        if ("camelquarkus".equals(TestConfiguration.product())) {
            customizeForQuarkus();
        } else {
            customizeDefault();
        }
    }

    private void customizeForQuarkus() {
        /*
            @Inject
            @MongoClientName("<name>")
            MongoClient mongoClient;
         */
        getRouteBuilderClassBuilder().addField(FieldSpec.builder(ClassName.get("com.mongodb.client", "MongoClient"), name)
            .addAnnotation(ClassName.get("javax.inject", "Inject"))
            .addAnnotation(AnnotationSpec.builder(ClassName.get("io.quarkus.mongodb", "MongoClientName"))
                .addMember("value", "$S", name)
                .build())
            .build());

        getApplicationProperties().put("quarkus.mongodb." + name + ".connection-string", replicaSetUrl);
    }

    private void customizeDefault() {
        /*
            bindToRegistry("<name>", MongoClients.create("<replicaSetUrl>");
         */
        getConfigureMethodBuilder().addStatement("bindToRegistry($S, $T.create($S))",
            name, ClassName.get("com.mongodb.client", "MongoClients"), replicaSetUrl);
    }
}
