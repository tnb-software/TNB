package software.tnb.docling.serve.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;

public abstract class DoclingServe extends Service<NoAccount, NoClient, NoValidation> implements WithDockerImage {

    protected static final int PORT = 5001;

    public abstract String url();

    @Override
    public String defaultImage() {
        return "quay.io/docling-project/docling-serve:v1.16.1";
    }
}
