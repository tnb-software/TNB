package software.tnb.hyperfoil.service;

import software.tnb.common.service.Service;
import software.tnb.hyperfoil.validation.HyperfoilValidation;

public abstract class Hyperfoil implements Service {
    public abstract String connection();

    public abstract String hyperfoilUrl();

    public HyperfoilValidation getValidation() {
        return new HyperfoilValidation(connection());
    }

    public abstract int getPortMapping(int port);
}
