package software.tnb.kudu.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.ConfigurableService;
import software.tnb.kudu.resource.client.KuduClient;
import software.tnb.kudu.service.configuration.KuduConfiguration;
import software.tnb.kudu.validation.KuduValidation;

import java.util.List;
import java.util.Optional;

public abstract class Kudu extends ConfigurableService<NoAccount, KuduClient, KuduValidation, KuduConfiguration> implements WithDockerImage {

    protected static final int TSERVER_RPC_PORT = 7050;
    protected static final int TSERVER_HTTP_PORT = 8050;
    protected static final int MASTER_RPC_PORT = 7051;
    protected static final int MASTER_HTTP_PORT = 8051;

    protected static final String MASTER_PREFIX = "kudu-master";
    protected static final String TSERVER_PREFIX = "kudu-tserver";

    public abstract List<String> getMastersUsingHttp();

    public abstract List<String> getTserversUsingHttp();

    public abstract List<String> getMastersUsingRpc();

    public abstract List<String> getTserversUsingRpc();

    @Override
    public String defaultImage() {
        return "quay.io/rh_integration/apache/kudu:1.17";
    }

    @Override
    protected void defaultConfiguration() {
        getConfiguration().withMasterNumber(3).withTabletNumber(3);
    }

    public KuduValidation validation() {
        return Optional.ofNullable(validation).orElseGet(() -> validation = new KuduValidation(client));
    }
}
