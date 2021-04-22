package org.jboss.fuse.tnb.log.service;

import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.log.validation.LogValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

@AutoService(Log.class)
public class Log implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Log.class);
    private List<LogValidation> toClose = new LinkedList<>();

    public LogValidation validation(Reader r) {
        //get a new reader for every validation
        LOG.debug("Creating new Log validation");
        LogValidation v = new LogValidation(r);
        toClose.add(v);
        return v;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        for (LogValidation v : toClose) {
            v.close();
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
    }
}
