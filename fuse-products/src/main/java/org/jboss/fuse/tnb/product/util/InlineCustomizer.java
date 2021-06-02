package org.jboss.fuse.tnb.product.util;

import org.jboss.fuse.tnb.customizer.Customizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.utils.StringEscapeUtils;

import java.util.Optional;
import java.util.Set;

/**
 * Inlines values from final fields to specified methods
 * Mainly serves as a workaround for ENTESB-16531.
 */
public class InlineCustomizer extends Customizer {

    private static final Set<String> INLINE_METHODS = Set.of("from", "to");
    private static final Logger LOG = LoggerFactory.getLogger(InlineCustomizer.class);

    @Override
    public void customize() {
        inlineVars();
    }

    private void inlineVars() {
        getConfigureMethod().getBody().ifPresent(body -> {
            //Visit all method calls & inline values if necessary
            body.accept(new GenericVisitorAdapter<>() {
                @Override
                public Object visit(MethodCallExpr n, Object arg) {
                    Object defaultVal = super.visit(n, arg);
                    if (INLINE_METHODS.contains(n.getNameAsString())) {
                        inlineVar(n);
                    }
                    return defaultVal;
                }
            }, null);
        });
    }

    /**
     * Tries to find the value of field name and return that value as expression
     *
     * @param name referenced field name
     * @param defaultValue current expression that is to be replaced
     * @return replaced expression with the value or the default value
     */
    private Expression replaceExpr(String name, Expression defaultValue) {
        final Optional<FieldDeclaration> field = getRouteBuilderClass().getFieldByName(name);
        final FieldDeclaration fieldDeclaration = field.orElseThrow(() -> new UnsupportedOperationException(String
            .format("Trying to inline field %s in class %s, but the field was not found", name,
                getRouteBuilderClass().getNameAsString())));
        if (!fieldDeclaration.isFinal()) {
            LOG.warn("Field declaration for inlining variables isn't final - values might not get passed from the test");
        }
        for (VariableDeclarator variable : fieldDeclaration.getVariables()) {
            if (variable.getNameAsString().equals(name)) {
                return variable.getInitializer().orElse(defaultValue);
            }
        }
        return defaultValue;
    }

    private void inlineVar(MethodCallExpr n) {
        n.getArguments().replaceAll(argExpr -> {
            //matches this.a or File.pathSeparator
            if (argExpr.isFieldAccessExpr()) {
                return argExpr.toFieldAccessExpr().map(fieldAccessExpr -> {
                    //is true if File.pathSeparator, is false for pathSeparator
                    if (fieldAccessExpr.hasScope()) {
                        //don't change access to other class' fields
                        if (!fieldAccessExpr.getScope().isThisExpr()) {
                            return fieldAccessExpr;
                        }
                    }
                    return replaceExpr(fieldAccessExpr.getNameAsString(), argExpr);
                }).orElse(argExpr);
            }
            //matches normal field access, fromEndpoint for example
            if (argExpr.isNameExpr()) {
                return argExpr.toNameExpr().map(nameExpr -> {
                    return replaceExpr(nameExpr.getNameAsString(), argExpr);
                }).orElse(argExpr);
            }
            //Matches any method call, is used to warn and transform basic format
            if (argExpr.isMethodCallExpr()) {
                final MethodCallExpr methodCallExpr = argExpr.asMethodCallExpr();
                if (methodCallExpr.getNameAsString().equals("format")) {
                    try {
                        LOG.warn(
                            "Trying to inline result of String.format in from/to... if you get an error you can move the format call the to field " +
                                "initializer/constructor");
                        StringLiteralExpr template = methodCallExpr.getArguments().get(0).asStringLiteralExpr();
                        //One argument is the formatting string
                        Object[] args = new Object[methodCallExpr.getArguments().size() - 1];
                        for (int i = 0; i < methodCallExpr.getArguments().size() - 1; i++) {
                            final Expression argument = methodCallExpr.getArgument(i + 1);
                            if (argument.isNameExpr()) {
                                final String fieldName = argument.asNameExpr().getNameAsString();
                                for (VariableDeclarator variable : getRouteBuilderClass().getFieldByName(fieldName).get().getVariables()) {
                                    if (variable.getNameAsString().equals(fieldName)) {
                                        args[i] = variable.getInitializer().orElse(new NullLiteralExpr());
                                    }
                                }
                            } else if (argument.isFieldAccessExpr()) {
                                LOG.error(
                                    "Can't reference fields from other classes inside format - please make a field or move this into constructor");
                            } else if (argument.isStringLiteralExpr()) {
                                args[i] = argument.asStringLiteralExpr().getValue();
                            } else if (argument.isBooleanLiteralExpr()) {
                                args[i] = argument.asBooleanLiteralExpr().getValue();
                            } else if (argument.isLongLiteralExpr()) {
                                args[i] = argument.asLongLiteralExpr().asNumber();
                            } else if (argument.isDoubleLiteralExpr()) {
                                args[i] = argument.asDoubleLiteralExpr().asDouble();
                            } else if (argument.isIntegerLiteralExpr()) {
                                args[i] = argument.asIntegerLiteralExpr().asNumber();
                            } else {
                                LOG.error("Unknown argument type {}", argument.toString());
                                args[i] = argument.toString();
                            }
                        }
                        //Perform the formatting before buildtime so camel-k can discover the component
                        return new StringLiteralExpr(StringEscapeUtils.escapeJava(String.format(template.asString(), args)));
                    } catch (Exception e) {
                        LOG.error("Failed to process String.format in route definition, please move the format call to a field");
                        throw e;
                    }
                } else {
                    LOG.warn("Calling methods in from/to methods can break Camel K component discovery, be careful");
                }
            }

            return argExpr;
        });
    }
}
