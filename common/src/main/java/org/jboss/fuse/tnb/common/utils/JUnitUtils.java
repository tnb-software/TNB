package org.jboss.fuse.tnb.common.utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.TestDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        try {
            Field f = Class.forName("org.junit.jupiter.engine.descriptor.AbstractExtensionContext").getDeclaredField("testDescriptor");
            // Get the root descriptor that has all the scheduled test classes
            TestDescriptor rootTestDescriptor = (TestDescriptor) ReflectionUtils.tryToReadFieldValue(f, extensionContext.getRoot()).get();
            // Get the current descriptor to get the currently executed test class
            TestDescriptor currentTestDescriptor = (TestDescriptor) ReflectionUtils.tryToReadFieldValue(f, extensionContext).get();
            // Save the list of test classes that are executed in current run
            List<? extends TestDescriptor> testClasses = new ArrayList<>(rootTestDescriptor.getChildren());
            boolean found = false;
            // For all of the remaining classes to be executed
            for (int i = testClasses.indexOf(currentTestDescriptor) + 1; i < testClasses.size(); i++) {
                // Get all fields annotated with @RegisterExtension and check if any of those is an instance of given class
                if (AnnotationSupport.findAnnotatedFieldValues(((ClassTestDescriptor) testClasses.get(i)).getTestClass(), RegisterExtension.class)
                    .stream().anyMatch(extensionClass::isInstance)) {
                    LOG.trace("Instance of {} found in {}", extensionClass.getSimpleName(),
                        ((ClassTestDescriptor) testClasses.get(i)).getTestClass().getSimpleName());
                    found = true;
                }
            }
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
