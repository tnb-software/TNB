package software.tnb.hyperfoil.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.hyperfoil.validation.HyperfoilValidation;

public abstract class Hyperfoil extends Service<NoAccount, NoClient, HyperfoilValidation> {
    public abstract String connection();

    public abstract String hyperfoilUrl();

    public HyperfoilValidation validation() {
        return new HyperfoilValidation(connection());
    }

    public abstract int getPortMapping(int port);
}
