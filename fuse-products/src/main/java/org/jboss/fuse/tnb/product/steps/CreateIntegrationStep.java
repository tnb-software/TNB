package org.jboss.fuse.tnb.product.steps;

import com.squareup.javapoet.CodeBlock;

import java.util.List;

public class CreateIntegrationStep extends Step {
    private String name;
    private CodeBlock routeDefinition;
    private String[] camelComponents;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CodeBlock getRouteDefinition() {
        return routeDefinition;
    }

    public void setRouteDefinition(CodeBlock routeDefinition) {
        this.routeDefinition = routeDefinition;
    }

    public String[] getCamelComponents() {
        return camelComponents;
    }

    public void setCamelComponents(String[] camelComponents) {
        this.camelComponents = camelComponents;
    }

    //overloaded constructors as user may need only subset of parameters for his product
    public CreateIntegrationStep(String name, CodeBlock routeDefinition, String... camelComponents){
        this.name = name;
        this.routeDefinition = routeDefinition;
        this.camelComponents = camelComponents;
    }

}
