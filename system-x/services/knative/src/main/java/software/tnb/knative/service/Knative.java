package software.tnb.knative.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.service.Service;
import software.tnb.knative.validation.KnativeValidation;

import io.fabric8.knative.client.KnativeClient;

public abstract class Knative extends Service<NoAccount, KnativeClient, KnativeValidation> {
    protected KnativeClient client() {
        if (OpenshiftClient.get().isAdaptable(KnativeClient.class)) {
            return OpenshiftClient.get().adapt(KnativeClient.class);
        }
        throw new IllegalArgumentException("Unable to adapt OpenshiftClient to KnativeClient");
    }

    @Override
    public KnativeValidation validation() {
        if (validation == null) {
            validation = new KnativeValidation(client());
        }
        return validation;
    }
}
