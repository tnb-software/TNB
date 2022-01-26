package org.jboss.fuse.tnb.customizer.mongodb;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.integration.Customizer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

public class MongoDBCustomizer extends Customizer {

    private final String name;
    private final String replicaSetUrl;

    public MongoDBCustomizer(String name, String replicaSetUrl) {
        this.name = name;
        this.replicaSetUrl = replicaSetUrl;
    }

    @Override
    public void customize() {
        if (TestConfiguration.product() == ProductType.CAMEL_SPRINGBOOT) {
            customizeSpringBoot();
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

    /**
     * getApplicationProperties().put("spring.data.mongodb.uri", replicaSetUrl);
     * could be used, but I lost so much time doing this method, I won't use it, they both work.
     */
    private void customizeSpringBoot() {
        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(TestConfiguration.appGroupId());

        compilationUnit.addImport("com.mongodb.client.MongoClients");
        compilationUnit.addImport("com.mongodb.client.MongoClient");
        compilationUnit.addImport("org.springframework.context.annotation.Bean");
        compilationUnit.addImport("org.springframework.context.annotation.Configuration");

        ClassOrInterfaceDeclaration mongoDBConfigurationClassDeclaration = compilationUnit.addClass("MongoDBConfiguration").setPublic(true);

        mongoDBConfigurationClassDeclaration.addAnnotation("Configuration");

        MethodDeclaration mongoClientMethodDeclaration = mongoDBConfigurationClassDeclaration.addMethod("mongoClient", Modifier.Keyword.PUBLIC);
        mongoClientMethodDeclaration.setType("MongoClient");
        mongoClientMethodDeclaration.addAnnotation("Bean");
        BlockStmt methodBody = new BlockStmt();
        methodBody.addStatement(String.format("return MongoClients.create(\"%s\");", replicaSetUrl));

        mongoClientMethodDeclaration.setBody(methodBody);

        getIntegrationBuilder().addClass(compilationUnit);
    }
}
