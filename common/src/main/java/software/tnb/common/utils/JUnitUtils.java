package software.tnb.common.utils;

import software.tnb.common.config.TestConfiguration;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.TestDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public final class JUnitUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JUnitUtils.class);

    private JUnitUtils() {
    }

    /**
     * Checks if the given extension is used in any of the following tests.
     *
     * @param extensionContext junit extension context
     * @param extensionClass extension implementation class
     * @return true if the extension is used in any of following tests, false otherwise
     */
    public static boolean isExtensionStillNeeded(ExtensionContext extensionContext, Class<?> extensionClass) {
        if (TestConfiguration.parallel()) {
            // In parallel, we can't guarantee that the extension will run in the same thread (== same namespace)
            return false;
        }
        try {
            Field f = Class.forName("org.junit.jupiter.engine.descriptor.AbstractExtensionContext").getDeclaredField("testDescriptor");
            // Get the root descriptor that has all the scheduled test classes
            TestDescriptor rootTestDescriptor = (TestDescriptor) ReflectionUtils.tryToReadFieldValue(f, extensionContext.getRoot()).get();
            // Get the current descriptor to get the currently executed test class
            TestDescriptor currentTestDescriptor = (TestDescriptor) ReflectionUtils.tryToReadFieldValue(f, extensionContext).get();
            // Save the list of test classes that are executed in current run
            List<? extends TestDescriptor> testClassesPlan = new ArrayList<>(rootTestDescriptor.getChildren());
            Set<ClassBasedTestDescriptor> testClasses = new HashSet<>();
            // For all of the remaining classes to be executed
            for (int i = testClassesPlan.indexOf(currentTestDescriptor) + 1; i < testClassesPlan.size(); i++) {
                // Get all fields annotated with @RegisterExtension and check if any of those is an instance of given class
                final ClassTestDescriptor classTestDescriptor = (ClassTestDescriptor) testClassesPlan.get(i);

                Stack<TestDescriptor> descriptorsToProcess = new Stack<>();
                descriptorsToProcess.add(classTestDescriptor);

                //Check for any level of nestedness and search for all class based test descriptors
                while (!descriptorsToProcess.empty()) {
                    final TestDescriptor desc = descriptorsToProcess.pop();
                    if (desc.isContainer()) {
                        descriptorsToProcess.addAll(desc.getChildren());
                    }
                    if (desc instanceof ClassBasedTestDescriptor) {
                        testClasses.add((ClassBasedTestDescriptor) desc);
                    }
                }
            }
            //Check all classes - nested and containers
            boolean found = testClasses.stream()
                .anyMatch(it -> AnnotationSupport.findAnnotatedFieldValues(it.getTestClass(), RegisterExtension.class)
                    .stream()
                    .anyMatch(extensionClass::isInstance)
                );
            if (!found) {
                LOG.debug("JUnit: No more usages of {} found", extensionClass.getSimpleName());
            } else {
                LOG.debug("JUnit: {} will be used in next tests", extensionClass.getSimpleName());
            }
            return found;
        } catch (Exception e) {
            LOG.debug("JUnit: Unable to check for extension class usages, returning false");
            LOG.trace("Exception while checking: ", e);
            return false;
        }
    }
}
