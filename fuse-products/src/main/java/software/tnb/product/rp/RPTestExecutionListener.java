package software.tnb.product.rp;

import software.tnb.common.config.TestConfiguration;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import com.google.auto.service.AutoService;

@AutoService(TestExecutionListener.class)
public class RPTestExecutionListener implements TestExecutionListener {

    private static String transformReportingName(String name) {
        return name.replace("()", "").replace("(", "{").replace(")", "}");
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (!TestConfiguration.reportPortalEnabled()) {
            return;
        }

        if (testIdentifier.isTest()) {
            Attachments.startTestCase(transformReportingName(testIdentifier.getLegacyReportingName()));
        } else {
            testIdentifier.getSource().ifPresent(source -> {
                if (source instanceof ClassSource) {
                    Attachments.startTestClass(((ClassSource) source).getClassName());
                }
            });
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier,
        TestExecutionResult testExecutionResult) {
        if (!TestConfiguration.reportPortalEnabled()) {
            return;
        }

        if (testIdentifier.isTest()) {
            Attachments.endTestCase(testExecutionResult.getThrowable().isPresent());
        } else {
            testIdentifier.getSource().ifPresent(source -> {
                if (source instanceof ClassSource) {
                    Attachments.endTestClass();
                }
            });
        }
    }
}
