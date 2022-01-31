package org.jboss.fuse.tnb.product.rp;

import org.jboss.fuse.tnb.common.config.TestConfiguration;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ReportPortalExtension implements AfterTestExecutionCallback, BeforeTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        if (TestConfiguration.reportPortalEnabled()) {
            Attachments.startTestClass(context.getRequiredTestClass().getName());
            context.getTestMethod().ifPresent(m -> {
                Attachments.startTestCase(m.getName());
            });
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        if (TestConfiguration.reportPortalEnabled()) {
            Attachments.endTestCase(context.getExecutionException().isPresent());
        }
    }
}
