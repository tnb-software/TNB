package software.tnb.common.account;

import static org.junit.jupiter.api.Assertions.fail;

import software.tnb.common.config.TestConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public final class Accounts {
    private static final Logger LOG = LoggerFactory.getLogger(Accounts.class);

    private static CredentialsLoader loader;

    private Accounts() {
    }

    /**
     * Create the instance of given account class with values populated from the credentials.
     *
     * @param accountClass account class to create instance of
     * @param <T> return type
     * @return new instance of given class
     */
    public static <T extends Account> T get(Class<T> accountClass) {
        Function<WithId, String> getId = WithId::getId;
        T instance;
        try {
            instance = accountClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create instance of " + accountClass.getName() + " class: ", e);
        }
        if (instance instanceof WithId) {
            LOG.debug("Loading {} account", accountClass.getSimpleName());
            String credentialsId = getId.apply((WithId) instance);
            if (loader == null) {
                try {
                    createLoader();
                } catch (Exception e) {
                    fail("Could not load credentials", e);
                }
            }
            T account = loader.get(credentialsId, accountClass);
            if (account == null) {
                throw new IllegalArgumentException("Credentials with id " + credentialsId + " not found in credentials.yaml file");
            }
            return account;
        } else {
            LOG.debug("Initialization of {}. No credentials loading needed.", accountClass.getSimpleName());
            return instance;
        }
    }

    private static void createLoader() throws Exception {
        if (TestConfiguration.useVault()) {
            if (TestConfiguration.vaultToken() != null) {
                LOG.info("Logging into vault using github token");
                loader = new VaultCredentialsLoader(
                    TestConfiguration.vaultAddress(),
                    TestConfiguration.vaultPathPattern(),
                    TestConfiguration.vaultToken()
                );
            } else {
                LOG.info("Logging into vault using approle");
                loader = new VaultCredentialsLoader(
                    TestConfiguration.vaultAddress(),
                    TestConfiguration.vaultPathPattern(),
                    TestConfiguration.vaultRoleId(),
                    TestConfiguration.vaultSecretId()
                );
            }
        } else {
            LOG.info("Loading credentials from file");
            loader = new YamlCredentialsLoader(TestConfiguration.credentialsFile());
        }
    }
}
