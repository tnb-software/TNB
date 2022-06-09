package software.tnb.product.customizer.component;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.config.TestConfiguration;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.component.mongodb.MongoDBCustomizer;

import org.junit.jupiter.api.Tag;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.Map;

@Tag("unit")
public class MongoDBCustomizerTest extends ProductCustomizerTestParent {
    private static final String REPLICA_SET_URL = "mongodb://localhost:27017";

    @Override
    public void validateQuarkus() {
        customizer.doCustomize();

        assertThat(ib.getProperties()).isEqualTo(Map.of("quarkus.mongodb.connection-string", REPLICA_SET_URL));
    }

    @Override
    public void validateCamelK() {
        customizer.doCustomize();

        assertThat(ib.getProperties()).isEqualTo(Map.of("quarkus.mongodb.connection-string", REPLICA_SET_URL));
    }

    @Override
    public void validateSpringBoot() {
        CompilationUnit expectedClass = new CompilationUnit();
        expectedClass.setPackageDeclaration(TestConfiguration.appGroupId());

        expectedClass.addImport("com.mongodb.client.MongoClients");
        expectedClass.addImport("com.mongodb.client.MongoClient");
        expectedClass.addImport("org.springframework.context.annotation.Bean");
        expectedClass.addImport("org.springframework.context.annotation.Configuration");

        ClassOrInterfaceDeclaration mongoDBConfigurationClassDeclaration = expectedClass.addClass("MongoDBConfiguration").setPublic(true);

        mongoDBConfigurationClassDeclaration.addAnnotation("Configuration");

        MethodDeclaration mongoClientMethodDeclaration = mongoDBConfigurationClassDeclaration.addMethod("mongoClient", Modifier.Keyword.PUBLIC);
        mongoClientMethodDeclaration.setType("MongoClient");
        mongoClientMethodDeclaration.addAnnotation("Bean");
        BlockStmt methodBody = new BlockStmt();
        methodBody.addStatement(String.format("return MongoClients.create(\"%s\");", REPLICA_SET_URL));

        mongoClientMethodDeclaration.setBody(methodBody);

        customizer.doCustomize();

        assertThat(ib.getAdditionalClasses()).hasSize(1);
        assertThat(ib.getAdditionalClasses().get(0).toString()).isEqualTo(expectedClass.toString());
    }

    @Override
    public Customizer newCustomizer() {
        return new MongoDBCustomizer(REPLICA_SET_URL);
    }
}
