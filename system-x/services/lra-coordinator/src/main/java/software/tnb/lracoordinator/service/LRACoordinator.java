package software.tnb.lracoordinator.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;

import java.util.Map;

public abstract class LRACoordinator extends Service<NoAccount, NoClient, NoValidation> implements WithDockerImage {

    public static final int DEFAULT_PORT = 8888;

    public Map<String, String> containerEnvironment() {
        return Map.of("QUARKUS_HTTP_PORT", String.valueOf(DEFAULT_PORT)
            , "LOG_LEVEL", "DEBUG", "quarkus.log.level", "DEBUG"
        );
    }

    public abstract String hostname();

    public abstract int port();

    public String defaultImage() {
        return "quay.io/jbosstm/lra-coordinator:latest";
    }

    public void openResources() {
    }

    public void closeResources() {
    }

    public abstract String getUrl();

    public abstract String getExternalUrl();

    public abstract String getLog();
}
