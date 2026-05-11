package software.tnb.common.account.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * Tries to load credentials from supplied loaders in order of the supplied list
 */
public class DelegatingCredentialsLoader extends CredentialsLoader {
    private static final Logger LOG = LoggerFactory.getLogger(DelegatingCredentialsLoader.class);

    private final List<CredentialsLoader> loaders;

    public DelegatingCredentialsLoader(List<CredentialsLoader> loaders) {
        this.loaders = loaders;
    }

    @Override
    public Object loadCredentials(String credentialsId) {
        Object credentials = null;
        for (CredentialsLoader loader : loaders) {
            LOG.debug("Trying to load {} account credentials using {}", credentialsId, loader.getClass().getSimpleName());
            try {
                credentials = loader.loadCredentials(credentialsId);
                if (credentials != null) {
                    break;
                }
            } catch (Exception e) {
                //thank you, next
            }
        }
        return credentials;
    }

    @Override
    public String toJson(Object credentials) {
        try {
            return mapper.writeValueAsString(credentials);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert credentials to json", e);
        }
    }
}
