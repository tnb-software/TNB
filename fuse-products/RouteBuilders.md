# Creating routes in TNB

TNB uses [javaparser](https://javaparser.org/) to parse and transform the code used for defining integrations in fuse products. 
This allows you to use the same DSL for defining routes and if any product needs a modification to the _base_ route it can do it programmatically.

There are some limitations to this approach though:
* All routebuilders must be named classes, using anonymous classes will fail
* All final fields values are inlined, if you don't want your fields processed don't make them final
* Only primitive values can be processed in the routebuilder
* Nested classes can't be static - they are extracted to be a top-level class which can't be static
* Everything has to be in the same file - if you need to define a processor you'll need to do it in the same file, this is Camel-K limitation,
_this can be fixed later by merging the classes_

## Examples

```java
class ExampleTest {
    
    @RegisterExtension
    public static Slack slack = ServiceFactory.create(Slack.class);
    
    public void testConsumer() {
        IntegrationBuilder integrationBase = new IntegrationBuilder("slack-to-log")
                .fromRouteBuilder(new ConsumerRouteBuilder(slack))
                .dependencies("slack", "bean");
    }

    class ConsumerRouteBuilder extends RouteBuilder {

        final String slackEndpoint;

        public ConsumerRouteBuilder(Slack slack) {
            slackEndpoint = String
                    .format("slack:%s?webhookUrl=%s&token=RAW(%s)", slack.account().channel(), slack.account().webhookUrl(), slack.account().token());
        }

        @Override
        public void configure() throws Exception {
            from(slackEndpoint)
                    .log("${body.text}");
        }
    }
}
```

In the tested application the `ConsumerRouteBuilder` will become `MyRouteBuilder` with following code
```java
package com.test;

import org.apache.camel.builder.RouteBuilder;

public class MyRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from(slackEndpoint).log("${body.text}");
    }

    final String slackEndpoint = "slack:tests?webhookUrl=<webhook-url-value>&token=RAW(<token-value>)";
}
```

## Developer info
The classes are represented as CompilationUnit - Compilation unit is basically one Java file. 
You can't use just [ClassOrInterfaceDeclaration](https://www.javadoc.io/doc/com.github.javaparser/javaparser-core/3.3.2/com/github/javaparser/ast/body/ClassOrInterfaceDeclaration.html) because you'd be missing info about imports and package.

If you need to modify the code you can use the `Node#accept` method. 

See simple examples: 
* [inverting ifs](https://javaparser.org/inverting-ifs-in-javaparser/)
* [Inspecting/visualizing the AST](https://javaparser.org/inspecting-an-ast/)

### Nested classes hacks
The nested class functionality is kinda hacky due to the nature that you can't just yank a nested class and make it a CompileUnit (which we need for imports and in general making it a file).
So there's a simple process to change a nested class into a top-level class: 
1) Find the parent class and make that a CompileUnit
2) Find the nested class (nothing hard just CompileUnit.getLocalDeclarationFromClassname(TypeName))
3) Make the nested class it's own ClassOrInterfaceDeclaration and with that gain new CompileUnit instance
4) Analyze all imports from the parent CompileUnit and check if the Class is referenced in that class, if yes add it to the new CompileUnit
5) Set a new package name and process the class as with other routebuilders

