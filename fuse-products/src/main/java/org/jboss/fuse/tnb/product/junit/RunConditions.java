package org.jboss.fuse.tnb.product.junit;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.HTTPUtils;
import org.jboss.fuse.tnb.product.cq.configuration.QuarkusConfiguration;
import org.jboss.fuse.tnb.product.junit.jira.Jira;
import org.jboss.fuse.tnb.product.junit.jira.Jiras;
import org.jboss.fuse.tnb.product.junit.product.RunOn;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RunConditions implements ExecutionCondition {
    private static final Logger LOG = LoggerFactory.getLogger(RunConditions.class);
    private static final String JIRA_URL_PREFIX = "https://issues.redhat.com/rest/api/latest/issue/";
    private static final Set<String> RUN_RESOLUTIONS = Set.of("Resolved", "Closed", "Done", "Validation Backlog", "In Validation");

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        ConditionEvaluationResult result;
        result = evaluateJiraAnnotations(context);
        if (result.isDisabled()) {
            return result;
        }
        return evaluateRunOnAnnotations(context);
    }

    /**
     * Evaluate @RunOn and related annotations.
     * <p>
     * If the class isn't annotated with any RunOn annotation, all superclasses are checked
     *
     * @param context extension context
     * @return condition evaluation result enabled/disabled
     */
    private ConditionEvaluationResult evaluateRunOnAnnotations(ExtensionContext context) {
        List<RunOn> annotations = new ArrayList<>();
        // Find the annotation in current element (test method / test class)
        getCurrentElementAnnotation(context, RunOn.class).ifPresent(annotations::add);

        // If there are no annotations and the current element isn't a test method (e.g. is a test class), search the annotation in parent classes
        // This enables to have @CamelKOnly annotation in CamelKTestSuiteParent and you don't have to add it to every class extending the parent
        if (annotations.isEmpty() && context.getTestMethod().isEmpty()) {
            try {
                Field f = Class.forName("org.junit.jupiter.engine.descriptor.AbstractExtensionContext").getDeclaredField("testDescriptor");
                ClassTestDescriptor descriptor = (ClassTestDescriptor) ReflectionUtils.tryToReadFieldValue(f, context).get();
                annotations.addAll(getSuperClassAnnotations(descriptor.getTestClass()));
            } catch (Exception e) {
                LOG.warn("Unable to get test descriptor, not processing annotations");
            }
        }

        return annotations.stream().anyMatch(a -> shouldSkip(a, context.getDisplayName())) ? ConditionEvaluationResult.disabled("Skipped")
            : ConditionEvaluationResult.enabled("Running");
    }

    /**
     * Evaluate @Jira annotations.
     *
     * @param context extension context
     * @return condition evaluation result enabled/disabled
     */
    private ConditionEvaluationResult evaluateJiraAnnotations(ExtensionContext context) {
        Optional<Jiras> annotation = getCurrentElementAnnotation(context, Jiras.class);
        // If there is no "Jiras" annotation (with > 1 Jira), there still can be a single Jira annotation
        if (annotation.isEmpty()) {
            Optional<Jira> optionalJira = getCurrentElementAnnotation(context, Jira.class);
            if (optionalJira.isPresent()) {
                return evaluateJiraAnnotation(context, optionalJira.get());
            }
        } else {
            Jira[] jiras = annotation.get().value();
            for (Jira jira : jiras) {
                ConditionEvaluationResult result = evaluateJiraAnnotation(context, jira);
                if (result.isDisabled()) {
                    return result;
                }
            }
        }
        return ConditionEvaluationResult.enabled("Running");
    }

    private ConditionEvaluationResult evaluateJiraAnnotation(ExtensionContext context, Jira jira) {
        // If the jira isn't reported against the current environment, run the test
        if (!jira.configuration().isCurrentEnv()) {
            return ConditionEvaluationResult.enabled("Running");
        }
        for (String jiraKey : jira.keys()) {
            LOG.trace("Checking JIRA {}", jiraKey);
            final HTTPUtils.Response response = HTTPUtils.getInstance().get(JIRA_URL_PREFIX + jiraKey);
            if (response.getResponseCode() == 200) {
                final String status = new JSONObject(response.getBody()).getJSONObject("fields").getJSONObject("status").get("name")
                    .toString();
                if (!RUN_RESOLUTIONS.contains(status)) {
                    LOG.debug("Skipping {}, JIRA {} is in {} state", context.getDisplayName(), jiraKey, status);
                    return ConditionEvaluationResult.disabled(String.format("JIRA %s is in %s state", jiraKey, status));
                }
            } else {
                LOG.warn("Jira response code was {}, not evaluating jira state", response.getResponseCode());
            }
        }
        return ConditionEvaluationResult.enabled("Running");
    }

    /**
     * Gets the annotation of a given type if it is present on the current element in the extension context.
     *
     * @param context extension context
     * @param clazz annotation class
     * @param <A> annotation type
     * @return optional with the annotation
     */
    private <A extends Annotation> Optional<A> getCurrentElementAnnotation(ExtensionContext context, Class<A> clazz) {
        return AnnotationSupport.findAnnotation(context.getElement(), clazz);
    }

    /**
     * Gets the @RunOn annotations from all super classes.
     *
     * @param clazz class where to start
     * @return list of @RunOn annotations
     */
    private List<RunOn> getSuperClassAnnotations(Class<?> clazz) {
        List<RunOn> annotations = new ArrayList<>();
        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null) {
            AnnotationSupport.findAnnotation(superClass, RunOn.class).ifPresent(annotations::add);
            superClass = superClass.getSuperclass();
        }
        return annotations;
    }

    /**
     * Checks if the current test / class should be skipped, based on @RunOn annotation.
     *
     * @param runOn annotation
     * @param name context name
     * @return true/false
     */
    private boolean shouldSkip(RunOn runOn, String name) {
        LOG.trace("Evaluating annotation {}", runOn);
        if (runOn.product().length != 0 && !Arrays.asList(runOn.product()).contains(TestConfiguration.product())) {
            LOG.debug("Skipping {}, should run only on {} (was: {})", name, runOn.product(), TestConfiguration.product());
            return true;
        }

        if (OpenshiftConfiguration.isOpenshift() && !runOn.openshift()) {
            LOG.debug("Skipping {}, should not run on OpenShift", name);
            return true;
        }

        if (!OpenshiftConfiguration.isOpenshift() && !runOn.local()) {
            LOG.debug("Skipping {}, should not run on local", name);
            return true;
        }

        if (QuarkusConfiguration.isQuarkusNative() && !runOn.quarkusNative()) {
            LOG.debug("Skipping {}, should not run in native", name);
            return true;
        }
        return false;
    }
}
