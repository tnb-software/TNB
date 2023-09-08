package software.tnb.snmp.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;

import java.util.Map;

public abstract class SnmpServer extends Service<NoAccount, NoClient, NoValidation> implements WithDockerImage {

    public static final int SNMPD_LISTENING_PORT = 1163;
    public static final int SNMPTRAPD_LISTENING_PORT = 1162;

    @Override
    public String defaultImage() {
        return "images.paas.redhat.com/fuseqe/snmp-ocp:latest";
    }

    public abstract String host();

    public abstract int port();

    public abstract String getLog();

    public abstract String trapHost();

    public abstract int trapPort();

    public Map<String, String> containerEnvironment() {
        return Map.of();
    }
}
