package org.jboss.fuse.tnb.product.util;

import org.jboss.fuse.tnb.product.ck.customizer.CamelKCustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import java.util.Optional;
import java.util.Set;

/**
 * Inlines values from final fields to specified methods
 * Mainly serves as a workaround for ENTESB-16531.
 */
public class InlineCustomizer extends CamelKCustomizer {

    private static final Set<String> INLINE_METHODS = Set.of("from", "to", "fromF", "toF");
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
     * Tries to find the value of field name and return that value as expression.
     *
     * @param name referenced field name
     * @param defaultValue current expression that is to be replaced
     * @return replaced expression with the value or the default value
     */
    private Expression replaceExpr(String name, Expression defaultValue) {
        final Optional<FieldDeclaration> field = getRouteBuilderClass().getFieldByName(name);
        if (field.isEmpty()) {
            //The field might be local value and there's no way to obtain it's value
            return defaultValue;
        }
        final FieldDeclaration fieldDeclaration = field.get();
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

    private Expression inlineExpression(Expression expression) {
        if (expression.isFieldAccessExpr()) {
            return expression.toFieldAccessExpr().map(fieldAccessExpr -> {
                //is true if File.pathSeparator, is false for pathSeparator
                if (fieldAccessExpr.hasScope()) {
                    //don't change access to other class' fields
                    if (!fieldAccessExpr.getScope().isThisExpr()) {
                        return fieldAccessExpr;
                    }
                }
                return replaceExpr(fieldAccessExpr.getNameAsString(), expression);
            }).orElse(expression);
        }
        //matches normal field access, fromEndpoint for example
        if (expression.isNameExpr()) {
            return expression.toNameExpr().map(nameExpr -> replaceExpr(nameExpr.getNameAsString(), expression)).orElse(expression);
        }
        if (expression.isBinaryExpr()) {
            return expression.toBinaryExpr().map(binaryExpr -> {
                binaryExpr.setLeft(inlineExpression(binaryExpr.getLeft()));
                binaryExpr.setRight(inlineExpression(binaryExpr.getRight()));
                return binaryExpr;
            }).orElse(expression.asBinaryExpr());
        }
        //Matches any method call, is used to warn and transform basic format
        if (expression.isMethodCallExpr()) {
            final MethodCallExpr methodCallExpr = expression.asMethodCallExpr();
            //true if parent node is from/to and not fromF/toF
            boolean isParentNonFormat = expression.getParentNode().isPresent()
                && expression.getParentNode().get() instanceof MethodCallExpr
                && !((MethodCallExpr) expression.getParentNode().get()).getNameAsString().matches("(?:to|from)F");
            //If String.format is used you want to use (to|from)F instead
            if (methodCallExpr.getNameAsString().equals("format") && isParentNonFormat) {
                try {
                    final MethodCallExpr parentCall = (MethodCallExpr) expression.getParentNode().get();
                    //from -> fromF, to -> toF
                    parentCall.setName(parentCall.getNameAsString() + "F");
                    NodeList<Expression> args = new NodeList<>(methodCallExpr.getArguments());
                    parentCall.setArguments(args);
                    inlineVar(parentCall);
                    return null;
                } catch (Exception e) {
                    LOG.error("Failed to process String.format in route definition, please move the format call to a field");
                    throw e;
                }
            } else {
                LOG.warn("Calling methods in from/to methods can break Camel K component discovery, be careful");
            }
        }

        return expression;
    }

    private void inlineVar(MethodCallExpr n) {
        n.getArguments().replaceAll(this::inlineExpression);
    }
}
