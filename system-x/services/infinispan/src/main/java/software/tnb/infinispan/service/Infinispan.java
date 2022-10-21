package software.tnb.infinispan.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.infinispan.resource.account.InfinispanAccount;

import java.util.Map;

public abstract class Infinispan implements Service, WithDockerImage {
    public static final int PORT = 11222;

    public abstract int getPortMapping();

    private InfinispanAccount account;

    public InfinispanAccount account() {
        if (account == null) {
            account = AccountFactory.create(InfinispanAccount.class);
        }
        return account;
    }

    public abstract String getHost();

    public Map<String, String> containerEnvironment() {
        return Map.of("USER", account().username(), "PASS", account().password());
    }

    @Override
    public String defaultImage() {
        return "registry.redhat.io/datagrid/datagrid-8-rhel8:latest";
    }
}
