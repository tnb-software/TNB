package org.jboss.fuse.tnb.product;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.product.customizer.Customizer;

import com.github.javaparser.ast.CompilationUnit;

public class AddClassCustomizer extends Customizer {

    private String className;

    public AddClassCustomizer(String className) {
        this.className = className;
    }

    @Override
    public void customize() {
        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(TestConfiguration.appGroupId());
        compilationUnit.addClass(className).setPublic(true);

        getIntegrationBuilder().addClass(compilationUnit);
    }
}
