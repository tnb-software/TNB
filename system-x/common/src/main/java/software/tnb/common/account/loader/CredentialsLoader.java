package software.tnb.common.account.loader;

import software.tnb.common.account.Account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public abstract class CredentialsLoader {
    private static final Logger LOG = LoggerFactory.getLogger(CredentialsLoader.class);

    protected final ObjectMapper mapper;

    public abstract Object loadCredentials(String credentialsId);

    public abstract String toJson(Object credentials);

    public CredentialsLoader() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T extends Account> T get(List<String> credentialsIds, Class<T> accountClass) {
        T account = null;
        try {
            for (String id : credentialsIds) {
                Object credentials = loadCredentials(id);
                if (credentials != null) {
                    if (account == null) {
                        LOG.trace("Creating {} instance from credentials {}", accountClass.getSimpleName(), id);
                        account = mapper.readValue(toJson(credentials), accountClass);
                    } else {
                        LOG.trace("Updating {} instance with credentials {}", accountClass.getSimpleName(), id);
                        mapper.readerForUpdating(account).readValue(toJson(credentials));
                    }
                } else {
                    LOG.trace("Account with id {} not found in credentials", id);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Couldnt get credentials from ids: " + String.join(",", credentialsIds), e);
        }

        if (account == null) {
            throw new IllegalArgumentException(String.format("Unable to create %s instance from credentials [%s]"
                + " - no credentials with given ids found", accountClass.getSimpleName(), String.join(",", credentialsIds)));
        }

        return account;
    }
}
