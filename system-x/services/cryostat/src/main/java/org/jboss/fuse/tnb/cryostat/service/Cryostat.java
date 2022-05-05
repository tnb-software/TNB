package org.jboss.fuse.tnb.cryostat.service;

import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.cryostat.validation.CryostatValidation;

import java.util.Optional;

public abstract class Cryostat implements Service {

    protected CryostatValidation validation;

    public abstract String connectionUrl();

    public abstract String commandUrl();

    public abstract CryostatClient client();

    public abstract int getPortMapping(int port);

    public CryostatValidation validation() {
        validation = Optional.ofNullable(validation)
            .orElseGet(() -> new CryostatValidation(client()));
        return validation;
    }
}
