package software.tnb.infinispan.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.util.VersionUtils;
import software.tnb.common.validation.NoValidation;
import software.tnb.infinispan.account.InfinispanAccount;

import java.util.Map;
import java.util.regex.Pattern;

public abstract class Infinispan extends Service<InfinispanAccount, NoClient, NoValidation> implements WithDockerImage {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(?:Infinispan|Data Grid) Server (\\S+)");

    public static final int PORT = 11222;

    @Override
    public String serviceVersion() {
        String version = VersionUtils.extractFromLogs(this, VERSION_PATTERN);
        return version != null ? version : super.serviceVersion();
    }

    public abstract int getPortMapping();

    public abstract String getHost();

    public Map<String, String> containerEnvironment() {
        return Map.of("USER", account().username(), "PASS", account().password());
    }

    public String defaultImage() {
        return "registry.redhat.io/datagrid/datagrid-8-rhel9:latest";
    }
}
