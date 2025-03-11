package software.tnb.infinispan.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;
import software.tnb.infinispan.account.InfinispanAccount;

import java.util.Map;

public abstract class Infinispan extends Service<InfinispanAccount, NoClient, NoValidation> {

    public static final String DEFAULT_IMAGE = "registry.redhat.io/datagrid/datagrid-8-rhel9:latest";

    public static final int PORT = 11222;

    public abstract int getPortMapping();

    public abstract String getHost();

    public Map<String, String> containerEnvironment() {
        return Map.of("USER", account().username(), "PASS", account().password());
    }
}
