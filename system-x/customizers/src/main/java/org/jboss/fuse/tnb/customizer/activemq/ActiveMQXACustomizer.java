package org.jboss.fuse.tnb.customizer.activemq;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.customizer.Customizer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

/**
 * This class should be remove if https://github.com/quarkusio/quarkus/issues/14871 resolved
 * And the ConnectionFactory could be integrated with TransactionManager
 */
public class ActiveMQXACustomizer extends Customizer {

    private String url;
    private String user;
    private String password;

    public ActiveMQXACustomizer(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public void customize() {
        if (TestConfiguration.product() == ProductType.CAMEL_QUARKUS) {
            CompilationUnit compilationUnit = new CompilationUnit();
            compilationUnit.setPackageDeclaration(TestConfiguration.appGroupId());

            compilationUnit.addImport("org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory");

            compilationUnit.addImport("org.jboss.narayana.jta.jms.ConnectionFactoryProxy");
            compilationUnit.addImport("org.jboss.narayana.jta.jms.TransactionHelperImpl");

            compilationUnit.addImport("javax.enterprise.context.Dependent");
            compilationUnit.addImport("javax.enterprise.inject.Produces");
            compilationUnit.addImport("javax.inject.Named");
            compilationUnit.addImport("javax.transaction.TransactionManager");

            ClassOrInterfaceDeclaration activeMQXAConfigurationClassDeclaration =
                compilationUnit.addClass("XAConnectionFactoryConfiguration").setPublic(true);

            activeMQXAConfigurationClassDeclaration.addAnnotation("Dependent");

            MethodDeclaration connectionFactoryMethodDeclaration =
                activeMQXAConfigurationClassDeclaration.addMethod("getXAConnectionFactory", Modifier.Keyword.PUBLIC);
            connectionFactoryMethodDeclaration.addParameter("TransactionManager", "tm");
            connectionFactoryMethodDeclaration.setType("ConnectionFactoryProxy");
            connectionFactoryMethodDeclaration.addAnnotation("Produces");
            NormalAnnotationExpr namedAnnotation = connectionFactoryMethodDeclaration.addAndGetAnnotation("Named");
            namedAnnotation.addPair("value", "\"xaConnectionFactory\"");

            BlockStmt methodBody = new BlockStmt();
            methodBody.addStatement("ActiveMQXAConnectionFactory cf = "
                + "new ActiveMQXAConnectionFactory(\"" + url + "\", \"" + user + "\", \"" + password + "\");");
            methodBody.addStatement("return new ConnectionFactoryProxy(cf, new TransactionHelperImpl(tm));");

            connectionFactoryMethodDeclaration.setBody(methodBody);

            getIntegrationBuilder().addClass(compilationUnit);
        }
    }
}

