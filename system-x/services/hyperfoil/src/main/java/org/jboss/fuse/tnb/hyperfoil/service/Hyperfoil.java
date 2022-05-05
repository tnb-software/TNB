package org.jboss.fuse.tnb.hyperfoil.service;

import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.hyperfoil.validation.HyperfoilValidation;

public abstract class Hyperfoil implements Service {
    public abstract String connection();

    public abstract String hyperfoilUrl();

    public HyperfoilValidation getValidation() {
        return new HyperfoilValidation(connection());
    }

    public abstract int getPortMapping(int port);
}
