package software.tnb.infinispan.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;
import software.tnb.infinispan.account.InfinispanAccount;

import java.util.Map;

public abstract class Infinispan extends Service<InfinispanAccount, NoClient, NoValidation> implements WithDockerImage {
    public static final int PORT = 11222;

    public abstract int getPortMapping();

    public abstract String getHost();

    public Map<String, String> containerEnvironment() {
        return Map.of("USER", account().username(), "PASS", account().password());
    }

    @Override
    public String defaultImage() {
        return "registry.redhat.io/datagrid/datagrid-8-rhel8:latest";
    }
}
