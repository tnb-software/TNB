package org.jboss.fuse.tnb.log.service;

import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.log.validation.LogValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

/**
 * @deprecated use App.getLog() for validation purposes.
 */
@AutoService(Log.class)
@Deprecated
public class Log implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Log.class);

    private LogValidation validation;

    public LogValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Log validation");
            validation = new LogValidation();
        }
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
    }
}
