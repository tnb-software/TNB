package software.tnb.common.account.loader;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * Tries to load credentials from supplied loaders in order of the supplied list
 */
public class DelegatingCredentialsLoader extends CredentialsLoader {

    private final List<CredentialsLoader> loaders;

    public DelegatingCredentialsLoader(List<CredentialsLoader> loaders) {
        this.loaders = loaders;
    }

    @Override
    public Object loadCredentials(String credentialsId) {
        for (CredentialsLoader loader : loaders) {
            try {
                return loader.loadCredentials(credentialsId);
            } catch (Exception e) {
                //thank you, next
            }
        }
        throw new IllegalArgumentException("Can't find credentials for id " + credentialsId);
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
